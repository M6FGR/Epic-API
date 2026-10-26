package dev.m6fgr.epic_api.events.epic_api.player;

import dev.m6fgr.epic_api.events.dispatch.IDispatchableEvent;
import dev.m6fgr.epic_api.events.dispatch.abstracts.DispatchableEvent;
import dev.m6fgr.epic_api.events.dispatch.marks.HybridEvent;
import dev.m6fgr.epic_api.events.epic_api.EpicAPIDispatchableEvents;
import yesman.epicfight.skill.SkillContainer;

@HybridEvent
public class CounterAttackDispatchableEvent extends DispatchableEvent implements IDispatchableEvent<CounterAttackDispatchableEvent> {
    private final SkillContainer skillContainer;

    public CounterAttackDispatchableEvent(SkillContainer skillContainer) {
        this.skillContainer = skillContainer;
    }

    public SkillContainer getSkillContainer() {
        return this.skillContainer;
    }


    @Override
    public CounterAttackDispatchableEvent post() {
       return EpicAPIDispatchableEvents.Player.COUNTER_ATTACK.postEvent(this);
    }
}
