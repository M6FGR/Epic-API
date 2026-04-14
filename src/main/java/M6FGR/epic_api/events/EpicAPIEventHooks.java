package M6FGR.epic_api.events;

import M6FGR.epic_api.events.entity.EntityPatchEventHook;
import M6FGR.epic_api.events.item.ExCapCapabilityRegistryEventHook;
import yesman.epicfight.api.event.EventHook;

public class EpicAPIEventHooks {
    public static class Registry {
        /* Combines:
        WeaponCapabilityPresetRegistryEvent
        ExCapabilityBuilderPopulationEvent
        ExCapMoveSetRegistryEvent
        ExCapDataRegistrationEvent
        ExCapBuilderCreationEvent
           into 1 event
        */
        public static final EventHook<ExCapCapabilityRegistryEventHook> MOVE_SET_CAPABILITY = EventHook.createEventHook();

        /* Combines:
           EntityPatchRegistryEvent
           RegisterPatchedRenderersEvent.AddEntity
           into 1 event
        */
        public static final EventHook<EntityPatchEventHook> ENTITY_PATCH = EventHook.createEventHook();
    }
}
