package M6FGR.epic_api.events.epic_api.registry;

import M6FGR.epic_api.builders.epicfight.EntityPatchBuilder;
import M6FGR.epic_api.builders.epicfight.EntityPatchBuilder.FullPatchEntry;
import M6FGR.epic_api.builders.epicfight.EntityPatchBuilder.PRendererConstructor;
import M6FGR.epic_api.events.epic_api.EpicAPIEventHooks;
import M6FGR.epic_api.events.IEventHook;
import M6FGR.epic_api.main.EpicAPI;
import com.google.common.collect.Maps;
import net.minecraft.world.entity.Entity;
import yesman.epicfight.api.client.event.EpicFightClientEventHooks;
import yesman.epicfight.api.client.event.types.registry.RegisterPatchedRenderersEvent;
import yesman.epicfight.api.event.EpicFightEventHooks;
import yesman.epicfight.api.event.Event;
import yesman.epicfight.api.event.types.registry.EntityPatchRegistryEvent;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;

import java.util.Map;
import java.util.function.Function;

public class EntityPatchEventHook extends Event implements IEventHook<EntityPatchEventHook> {
    private final Map<FullPatchEntry<?>, PRendererConstructor> entityPatchMap = Maps.newHashMap();

    public void registerFrom(EntityPatchBuilder registrar) {
        for (FullPatchEntry<?> entry : registrar.getEntries()) {
            this.entityPatchMap.put(entry, entry.pRendererConstructor());
        }
    }

    @Override
    public EntityPatchEventHook post() {
        EpicFightEventHooks.Registry.ENTITY_PATCH.registerEvent(event -> {
            if (this.entityPatchMap.keySet().isEmpty()) {
                EpicAPI.debugIfDevSide("No entity patches were found in the map, skipping!");
                return;
            }
            // entity patches registry
            for (FullPatchEntry<?> entry : this.entityPatchMap.keySet()) {
                this.registerSingle(event, entry);
            }

        });
        EpicFightClientEventHooks.Registry.ADD_PATCHED_ENTITY.registerEvent(event -> {
            if (this.entityPatchMap.values().isEmpty()) {
                EpicAPI.debugIfDevSide("No renderers found in map, skipping!");
                return;
            }
            // Use the map to get the factory or just use the entry
            for (FullPatchEntry<?> entry : this.entityPatchMap.keySet()) {
                this.addSingleRenderer(event, entry);
            }
        });
        return EpicAPIEventHooks.Registry.ENTITY_PATCH.post(this);
    }

    @SuppressWarnings("unchecked")
    private <E extends Entity> void registerSingle(EntityPatchRegistryEvent event, FullPatchEntry<E> entry) {
        // Step down to a raw Function to erase the '? extends EntityPatch<Entity>' wildcard capture
        Function rawConstructor = entry.patchConstructor();

        // Cast the raw function cleanly to the strict format Epic Fight expects
        Function<E, EntityPatch<E>> strictConstructor = (Function<E, EntityPatch<E>>) rawConstructor;

        event.registerEntityPatch(entry.type(), strictConstructor);
    }

    private <E extends Entity> void addSingleRenderer(RegisterPatchedRenderersEvent.AddEntity event, FullPatchEntry<E> entry) {
        event.addPatchedEntityRenderer(entry.type(), (entityType) -> entry.pRendererConstructor().create(event.getContext(), entityType)
        );
    }

}
