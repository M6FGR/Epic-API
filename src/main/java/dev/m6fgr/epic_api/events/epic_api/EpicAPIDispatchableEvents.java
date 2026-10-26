package dev.m6fgr.epic_api.events.epic_api;

import dev.m6fgr.epic_api.cls.lists.ArraysList;
import dev.m6fgr.epic_api.events.dispatch.abstracts.DispatchableEvent;
import dev.m6fgr.epic_api.events.dispatch.dispatchers.CancelableEventDispatch;
import dev.m6fgr.epic_api.events.dispatch.dispatchers.EventDispatch;
import dev.m6fgr.epic_api.events.epic_api.entity.EntityInteractDispatchableEvent;
import dev.m6fgr.epic_api.events.epic_api.player.AddPlayerMotionsDispatchableEvent;
import dev.m6fgr.epic_api.events.epic_api.player.CounterAttackDispatchableEvent;
import dev.m6fgr.epic_api.events.epic_api.player.HeavyAttackDispatchableEvent;
import dev.m6fgr.epic_api.events.epic_api.registry.CameraAnimationRegistryDispatchableEvent;
import dev.m6fgr.epic_api.events.epic_api.registry.EntityPatchDispatchableEvent;
import dev.m6fgr.epic_api.events.epic_api.skill.AddGuardMotionsDispatchableEvent;

public class EpicAPIDispatchableEvents {

    private static final ArraysList<EventDispatch<?>> EVENTS = new ArraysList<>();

    public static class Registry {
        private Registry(){}
        /* combines:
           EntityPatchRegistryEvent
           RegisterPatchedRenderersEvent.AddEntity
           into 1 event
        */
        public static final EventDispatch<EntityPatchDispatchableEvent> ENTITY_PATCH = EventDispatch.createDispatchable();
        // registers camera animations
        public static final EventDispatch<CameraAnimationRegistryDispatchableEvent> CAMERA_ANIMATIONS = EventDispatch.createDispatchable();
    }
    public EpicAPIDispatchableEvents() {}

    public static class Player {
        private Player() {}

        // Adds more default motions for the player, specifically in the tail of PlayerPatch#initAnimation();
        public static final EventDispatch<AddPlayerMotionsDispatchableEvent> ADD_MOTIONS = EventDispatch.createDispatchable();
        // fires when the player heavy attacks if a heavy combo is present
        public static final CancelableEventDispatch<HeavyAttackDispatchableEvent> HEAVY_ATTACK = CancelableEventDispatch.createDispatchable();
        // fires when the player counter-attacks if a counter is present
        public static final CancelableEventDispatch<CounterAttackDispatchableEvent> COUNTER_ATTACK = CancelableEventDispatch.createDispatchable();
    }


    public static class Skill {
        private Skill() {}
        // adds guard motions and guard break motions for your weapon
        public static final EventDispatch<AddGuardMotionsDispatchableEvent.Normal> ADD_NORMAL_GUARD_MOTIONS = EventDispatch.createDispatchable();
        // adds parrying motions for your weapon
        public static final EventDispatch<AddGuardMotionsDispatchableEvent.Parry> ADD_PARRY_GUARD_MOTIONS = EventDispatch.createDispatchable();

    }

    public static class Entity {
        public static final EventDispatch<EntityInteractDispatchableEvent> ENTITY_INTERACT = EventDispatch.createDispatchable();
    }

    public static ArraysList<EventDispatch<? extends DispatchableEvent>> getEventsAsList() {
        return EVENTS;
    }

    static {
        EVENTS.addAsArray(
                Registry.ENTITY_PATCH
        );
        EVENTS.addAsArray(
                Player.ADD_MOTIONS,
                Player.HEAVY_ATTACK,
                Player.COUNTER_ATTACK
        );
        EVENTS.addAsArray(
                Skill.ADD_NORMAL_GUARD_MOTIONS,
                Skill.ADD_PARRY_GUARD_MOTIONS
        );
    }

}
