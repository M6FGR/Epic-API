package dev.m6fgr.epic_api.events.epic_api.player;

import dev.m6fgr.epic_api.events.dispatch.IDispatchableEvent;
import dev.m6fgr.epic_api.events.dispatch.abstracts.DispatchEvent;
import dev.m6fgr.epic_api.events.dispatch.marks.HybridEvent;
import dev.m6fgr.epic_api.events.epic_api.EpicAPIDispatchableEvents;
import yesman.epicfight.skill.SkillContainer;


@HybridEvent
public class HeavyAttackDispatchEvent extends DispatchEvent implements IDispatchableEvent<HeavyAttackDispatchEvent> {
    protected final SkillContainer skillContainer;

    public HeavyAttackDispatchEvent(SkillContainer skillContainer) {
        this.skillContainer = skillContainer;
    }

    public SkillContainer getSkillContainer() {
        return this.skillContainer;
    }

    @Override
    public HeavyAttackDispatchEvent post() {
       return EpicAPIDispatchableEvents.Player.HEAVY_ATTACK.postEvent(this);
    }
}
