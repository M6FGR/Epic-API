package M6FGR.epic_api.events.epic_api;

import M6FGR.epic_api.events.epic_api.player.AddPlayerMotionsEventHook;
import M6FGR.epic_api.events.epic_api.player.CounterAttackEvent;
import M6FGR.epic_api.events.epic_api.player.HeavyAttackEvent;
import M6FGR.epic_api.events.epic_api.registry.EntityPatchEventHook;
import yesman.epicfight.api.event.CancelableEventHook;
import yesman.epicfight.api.event.EventHook;

public class EpicAPIEventHooks {


    public static class Registry {
        private Registry(){}
        /* Combines:
           EntityPatchRegistryEvent
           RegisterPatchedRenderersEvent.AddEntity
           into 1 event
        */
        public static final EventHook<EntityPatchEventHook> ENTITY_PATCH = EventHook.createEventHook();
    }
    public EpicAPIEventHooks() {}

    public static class Player {
        private Player() {}

        public static final EventHook<AddPlayerMotionsEventHook> ADD_MOTIONS = EventHook.createEventHook();
        public static final CancelableEventHook<HeavyAttackEvent> HEAVY_ATTACK = CancelableEventHook.createCancelableEventHook();
        public static final CancelableEventHook<CounterAttackEvent> COUNTER_ATTACK = CancelableEventHook.createCancelableEventHook();
    }
}
