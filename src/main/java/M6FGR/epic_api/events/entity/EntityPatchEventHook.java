package M6FGR.epic_api.events.entity;

import M6FGR.epic_api.builders.epicfight.EntityPatchBuilder;
import M6FGR.epic_api.builders.epicfight.EntityPatchBuilder.FullPatchEntry;
import M6FGR.epic_api.builders.epicfight.EntityPatchBuilder.PRendererConstructor;
import M6FGR.epic_api.events.EpicAPIEventHooks;
import M6FGR.epic_api.events.IEventHook;
import M6FGR.epic_api.main.EpicAPI;
import com.google.common.collect.Maps;
import net.minecraft.world.entity.Entity;
import yesman.epicfight.api.client.event.EpicFightClientEventHooks;
import yesman.epicfight.api.client.event.types.registry.RegisterPatchedRenderersEvent;
import yesman.epicfight.api.event.EpicFightEventHooks;
import yesman.epicfight.api.event.Event;
import yesman.epicfight.api.event.types.registry.EntityPatchRegistryEvent;

import java.util.Map;

public class EntityPatchEventHook extends Event implements IEventHook {
    private final Map<FullPatchEntry<?>, PRendererConstructor> entityPatchMap = Maps.newHashMap();


    public <E extends Entity> void registerFrom(EntityPatchBuilder<E> registrar) {
        for (FullPatchEntry<?> entry : registrar.getEntries()) {
            this.entityPatchMap.put(entry, entry.pRendererConstructor());
        }
    }


    @Override
    public void post() {
        // 1. Register Patches
        EpicFightEventHooks.Registry.ENTITY_PATCH.registerEvent(event -> {
            if (this.entityPatchMap.keySet().isEmpty()) {
                EpicAPI.warn("No entity patches found in map, skipping!");
                return;
            }
            // Iterate over the keys (FullPatchEntry)
            for (FullPatchEntry<?> entry : this.entityPatchMap.keySet()) {
                this.registerSingle(event, entry);
            }
        });
        EpicAPIEventHooks.Registry.ENTITY_PATCH.post(this);
    }

    @Override
    public void postClient() {
        EpicFightClientEventHooks.Registry.ADD_PATCHED_ENTITY.registerEvent(event -> {
            if (this.entityPatchMap.values().isEmpty()) {
                EpicAPI.warn("No renderers found in map, skipping!");
                return;
            }
            // Use the map to get the factory or just use the entry
            for (FullPatchEntry<?> entry : this.entityPatchMap.keySet()) {
                this.addSingleRenderer(event, entry);
            }
        });
    }

    private <E extends Entity> void registerSingle(EntityPatchRegistryEvent event, FullPatchEntry<E> entry) {
        event.registerEntityPatch(entry.type(), entry.patchConstructor());
    }

    private <E extends Entity> void addSingleRenderer(RegisterPatchedRenderersEvent.AddEntity event, FullPatchEntry<E> entry) {
        event.addPatchedEntityRenderer(entry.type(), (entityType) ->
                entry.pRendererConstructor().create(event.getContext(), entityType)
        );
    }
}
