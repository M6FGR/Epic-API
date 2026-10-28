package dev.m6fgr.epic_api.events.epic_api.registry;

import com.google.common.collect.Maps;
import dev.m6fgr.epic_api.builders.epicfight.EntityPatchBuilder;
import dev.m6fgr.epic_api.builders.epicfight.EntityPatchBuilder.FullPatchEntry;
import dev.m6fgr.epic_api.builders.epicfight.EntityPatchBuilder.PRendererConstructor;
import dev.m6fgr.epic_api.events.dispatch.IDispatchableEvent;
import dev.m6fgr.epic_api.events.dispatch.abstracts.DispatchableEvent;
import dev.m6fgr.epic_api.events.dispatch.marks.HybridEvent;
import dev.m6fgr.epic_api.events.epic_api.EpicAPIDispatchableEvents;
import dev.m6fgr.epic_api.main.EpicAPIMod;
import net.minecraft.world.entity.Entity;
import yesman.epicfight.api.client.event.EpicFightClientEventHooks;
import yesman.epicfight.api.client.event.types.registry.RegisterPatchedRenderersEvent;
import yesman.epicfight.api.event.EpicFightEventHooks;
import yesman.epicfight.api.event.types.registry.EntityPatchRegistryEvent;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;

import java.util.Map;
import java.util.function.Function;


@HybridEvent
public class EntityPatchDispatchableEvent extends DispatchableEvent implements IDispatchableEvent<EntityPatchDispatchableEvent> {
    private final Map<FullPatchEntry<?>, PRendererConstructor> entityPatchMap = Maps.newHashMap();

    public void registerFrom(EntityPatchBuilder registrar) {
        for (FullPatchEntry<?> entry : registrar.getEntries()) {
            this.entityPatchMap.put(entry, entry.pRendererConstructor());
        }
    }

    @Override
    public EntityPatchDispatchableEvent post() {
        EpicFightEventHooks.Registry.ENTITY_PATCH.registerEvent(event -> {
            if (this.entityPatchMap.keySet().isEmpty()) {
                EpicAPIMod.debugIfDevSide("No entity patches were found in the map, skipping!");
                return;
            }
            // entity patches registry
            for (FullPatchEntry<?> entry : this.entityPatchMap.keySet()) {
                this.registerSingle(event, entry);
            }

        });
        EpicFightClientEventHooks.Registry.ADD_PATCHED_ENTITY.registerEvent(event -> {
            if (this.entityPatchMap.values().isEmpty()) {
                EpicAPIMod.debugIfDevSide("No renderers found in map, skipping!");
                return;
            }
            // Use the map to get the factory or just use the entry
            for (FullPatchEntry<?> entry : this.entityPatchMap.keySet()) {
                this.addSingleRenderer(event, entry);
            }
        });
        return EpicAPIDispatchableEvents.Registry.ENTITY_PATCH.postEvent(this);
    }


    @SuppressWarnings({"unchecked", "raw"})
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
