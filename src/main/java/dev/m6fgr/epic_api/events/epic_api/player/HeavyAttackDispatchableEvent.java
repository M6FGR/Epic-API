package dev.m6fgr.epic_api.events.epic_api.player;

import dev.m6fgr.epic_api.events.dispatch.IDispatchableEvent;
import dev.m6fgr.epic_api.events.dispatch.abstracts.DispatchableEvent;
import dev.m6fgr.epic_api.events.dispatch.marks.HybridEvent;
import dev.m6fgr.epic_api.events.epic_api.EpicAPIDispatchableEvents;
import yesman.epicfight.skill.SkillContainer;


@HybridEvent
public class HeavyAttackDispatchableEvent extends DispatchableEvent implements IDispatchableEvent<HeavyAttackDispatchableEvent> {
    protected final SkillContainer skillContainer;

    public HeavyAttackDispatchableEvent(SkillContainer skillContainer) {
        this.skillContainer = skillContainer;
    }

    public SkillContainer getSkillContainer() {
        return this.skillContainer;
    }

    @Override
    public HeavyAttackDispatchableEvent post() {
       return EpicAPIDispatchableEvents.Player.HEAVY_ATTACK.postEvent(this);
    }
}
