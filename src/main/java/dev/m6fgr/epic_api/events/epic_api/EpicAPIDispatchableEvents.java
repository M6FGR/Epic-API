package dev.m6fgr.epic_api.events.epic_api;

import dev.m6fgr.epic_api.events.dispatch.dispatchers.CancelableEventDispatch;
import dev.m6fgr.epic_api.events.dispatch.dispatchers.EventDispatch;
import dev.m6fgr.epic_api.events.epic_api.entity.EntityInteractDispatchEvent;
import dev.m6fgr.epic_api.events.epic_api.player.AddPlayerMotionsDispatchEvent;
import dev.m6fgr.epic_api.events.epic_api.player.CounterAttackDispatchEvent;
import dev.m6fgr.epic_api.events.epic_api.player.HeavyAttackDispatchEvent;
import dev.m6fgr.epic_api.events.epic_api.registry.CameraAnimationRegistryDispatchEvent;
import dev.m6fgr.epic_api.events.epic_api.registry.EntityPatchDispatchEvent;
import dev.m6fgr.epic_api.events.epic_api.skill.AddGuardMotionsDispatchEvent;

public class EpicAPIDispatchableEvents {

    public static class Registry {
        private Registry(){}
        /* combines:
           EntityPatchRegistryEvent
           RegisterPatchedRenderersEvent.AddEntity
           into 1 event
        */
        public static final EventDispatch<EntityPatchDispatchEvent> ENTITY_PATCH = EventDispatch.createDispatchable();
        // registers camera animations
        public static final EventDispatch<CameraAnimationRegistryDispatchEvent> CAMERA_ANIMATIONS = EventDispatch.createDispatchable();
    }
    public EpicAPIDispatchableEvents() {}

    public static class Player {
        private Player() {}

        // Adds more default motions for the player, specifically in the tail of PlayerPatch#initAnimation();
        public static final EventDispatch<AddPlayerMotionsDispatchEvent> ADD_MOTIONS = EventDispatch.createDispatchable();
        // fires when the player heavy attacks if a heavy combo is present
        public static final CancelableEventDispatch<HeavyAttackDispatchEvent> HEAVY_ATTACK = CancelableEventDispatch.createDispatchable();
        // fires when the player counter-attacks if a counter is present
        public static final CancelableEventDispatch<CounterAttackDispatchEvent> COUNTER_ATTACK = CancelableEventDispatch.createDispatchable();
    }


    public static class Skill {
        private Skill() {}
        // adds guard motions and guard break motions for your weapon
        public static final EventDispatch<AddGuardMotionsDispatchEvent.Normal> ADD_NORMAL_GUARD_MOTIONS = EventDispatch.createDispatchable();
        // adds parrying motions for your weapon
        public static final EventDispatch<AddGuardMotionsDispatchEvent.Parry> ADD_PARRY_GUARD_MOTIONS = EventDispatch.createDispatchable();

    }

    public static class Entity {
        public static final EventDispatch<EntityInteractDispatchEvent> ENTITY_INTERACT = EventDispatch.createDispatchable();
    }


}
