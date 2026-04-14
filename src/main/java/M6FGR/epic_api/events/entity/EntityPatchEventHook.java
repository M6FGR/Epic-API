package M6FGR.epic_api.events.entity;

import M6FGR.epic_api.builders.epicfight.EntityPatchRegistrar;
import M6FGR.epic_api.builders.epicfight.EntityPatchRegistrar.FullPatchEntry;
import M6FGR.epic_api.builders.epicfight.EntityPatchRegistrar.RendererFactory;
import M6FGR.epic_api.events.EpicAPIEventHooks.Registry;
import M6FGR.epic_api.events.IEventHook;
import M6FGR.epic_api.main.EpicAPI;
import net.minecraft.world.entity.Entity;
import yesman.epicfight.api.client.event.EpicFightClientEventHooks;
import yesman.epicfight.api.client.event.types.registry.RegisterPatchedRenderersEvent;
import yesman.epicfight.api.event.EpicFightEventHooks;
import yesman.epicfight.api.event.Event;
import yesman.epicfight.api.event.types.registry.EntityPatchRegistryEvent;

import java.util.HashMap;
import java.util.Map;

public class EntityPatchEventHook extends Event implements IEventHook {
    private final Map<FullPatchEntry<?>, RendererFactory> entityPatchMap = new HashMap<>();


    public <E extends Entity> void registerFrom(EntityPatchRegistrar<E> registrar) {
        for (FullPatchEntry<?> entry : registrar.getEntries()) {
            this.entityPatchMap.put(entry, entry.rendererFactory());
        }
    }


    @Override
    public void post() {
        // 1. Register Patches
        EpicFightEventHooks.Registry.ENTITY_PATCH.registerEvent(event -> {
            if (this.entityPatchMap.isEmpty()) {
                EpicAPI.LOGGER.warn("No entity patches found in map, skipping!");
                return;
            }
            // Iterate over the keys (FullPatchEntry)
            for (FullPatchEntry<?> entry : this.entityPatchMap.keySet()) {
                registerSingle(event, entry);
            }
        });

        // 2. Register Renderers
        EpicFightClientEventHooks.Registry.ADD_PATCHED_ENTITY.registerEvent(event -> {
            if (this.entityPatchMap.isEmpty()) {
                EpicAPI.LOGGER.warn("No renderers found in map, skipping!");
                return;
            }
            // Use the map to get the factory or just use the entry
            for (FullPatchEntry<?> entry : this.entityPatchMap.keySet()) {
                this.addSingleRenderer(event, entry);
            }
        });

        Registry.ENTITY_PATCH.post(this);
    }

    private <E extends Entity> void registerSingle(EntityPatchRegistryEvent event, FullPatchEntry<E> entry) {
        event.registerEntityPatch(entry.type(), entry.patchConstructor());
    }

    private <E extends Entity> void addSingleRenderer(RegisterPatchedRenderersEvent.AddEntity event, FullPatchEntry<E> entry) {
        event.addPatchedEntityRenderer(entry.type(), (entityType) ->
                entry.rendererFactory().create(event.getContext(), entityType)
        );
    }
}
