package dev.m6fgr.epic_api.events.epic_api.skill;

import dev.m6fgr.epic_api.events.dispatch.IDispatchableEvent;
import dev.m6fgr.epic_api.events.dispatch.abstracts.DispatchableEvent;
import dev.m6fgr.epic_api.events.epic_api.EpicAPIDispatchableEvents;
import yesman.epicfight.skill.guard.GuardSkill;
import yesman.epicfight.skill.guard.GuardSkill.Builder;
import yesman.epicfight.skill.guard.ParryingSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.WeaponCategory;

import java.util.function.BiFunction;


public abstract class AddGuardMotionsDispatchableEvent extends DispatchableEvent implements IDispatchableEvent<AddGuardMotionsDispatchableEvent> {

    protected final GuardSkill.Builder builder;

    public AddGuardMotionsDispatchableEvent(GuardSkill.Builder builder) {
        this.builder = builder;
    }

    public Builder getBuilder() {
        return this.builder;
    }

    public abstract AddGuardMotionsDispatchableEvent post();


    public static class Normal extends AddGuardMotionsDispatchableEvent {

        public Normal(Builder builder) {
            super(builder);
        }


        public void addGuardMotion(WeaponCategory category, BiFunction<CapabilityItem, PlayerPatch<?>, ?> function) {
            this.builder.addGuardMotion(category, function);
        }

        public void addGuardBreakMotion(WeaponCategory category, BiFunction<CapabilityItem, PlayerPatch<?>, ?> function) {
            this.builder.addGuardBreakMotion(category, function);
        }

        @Override
        public AddGuardMotionsDispatchableEvent.Normal post() {
            return EpicAPIDispatchableEvents.Skill.ADD_NORMAL_GUARD_MOTIONS.postEvent(this);
        }
    }


    public static class Parry extends AddGuardMotionsDispatchableEvent {

        public Parry(ParryingSkill.Builder builder) {
            super(builder);
        }


        public void addAdvancedGuardMotion(WeaponCategory category, BiFunction<CapabilityItem, PlayerPatch<?>, ?> function) {
            this.builder.addAdvancedGuardMotion(category, function);
        }

        @Override
        public AddGuardMotionsDispatchableEvent.Parry post() {
            return EpicAPIDispatchableEvents.Skill.ADD_PARRY_GUARD_MOTIONS.postEvent(this);
        }
    }
}
