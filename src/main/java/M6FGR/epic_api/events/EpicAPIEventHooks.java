package M6FGR.epic_api.events;

import M6FGR.epic_api.events.player.CounterAttackEvent;
import M6FGR.epic_api.events.player.HeavyAttackEvent;
import M6FGR.epic_api.events.registry.EntityPatchEventHook;
import yesman.epicfight.api.event.EventHook;

public class EpicAPIEventHooks {
    public static class Registry {

        /* Combines:
           EntityPatchRegistryEvent
           RegisterPatchedRenderersEvent.AddEntity
           into 1 event
        */
        public static final EventHook<EntityPatchEventHook> ENTITY_PATCH = EventHook.createEventHook();
        private Registry(){}
    }
    private EpicAPIEventHooks() {}

    public static class Player {
        private Player() {}

        public static final EventHook<HeavyAttackEvent> HEAVY_ATTACK = EventHook.createEventHook();
        public static final EventHook<CounterAttackEvent> COUNTER_ATTACK = EventHook.createEventHook();
    }
}
