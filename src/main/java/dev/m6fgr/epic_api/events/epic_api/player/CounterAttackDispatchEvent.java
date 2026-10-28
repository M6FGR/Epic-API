package dev.m6fgr.epic_api.events.epic_api.player;

import dev.m6fgr.epic_api.events.dispatch.IDispatchableEvent;
import dev.m6fgr.epic_api.events.dispatch.abstracts.DispatchEvent;
import dev.m6fgr.epic_api.events.dispatch.marks.HybridEvent;
import dev.m6fgr.epic_api.events.epic_api.EpicAPIDispatchableEvents;
import yesman.epicfight.skill.SkillContainer;

@HybridEvent
public class CounterAttackDispatchEvent extends DispatchEvent implements IDispatchableEvent<CounterAttackDispatchEvent> {
    private final SkillContainer skillContainer;

    public CounterAttackDispatchEvent(SkillContainer skillContainer) {
        this.skillContainer = skillContainer;
    }

    public SkillContainer getSkillContainer() {
        return this.skillContainer;
    }


    @Override
    public CounterAttackDispatchEvent post() {
       return EpicAPIDispatchableEvents.Player.COUNTER_ATTACK.postEvent(this);
    }
}
