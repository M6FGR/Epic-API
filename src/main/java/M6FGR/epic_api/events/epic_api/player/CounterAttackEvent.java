package M6FGR.epic_api.events.epic_api.player;

import M6FGR.epic_api.events.epic_api.EpicAPIEventHooks;
import M6FGR.epic_api.events.IEventHook;
import yesman.epicfight.api.event.CancelableEvent;
import yesman.epicfight.api.event.LivingEntityPatchEvent;
import yesman.epicfight.skill.SkillContainer;

public class CounterAttackEvent extends LivingEntityPatchEvent implements IEventHook<CounterAttackEvent>, CancelableEvent {
    private final SkillContainer skillContainer;

    public CounterAttackEvent(SkillContainer skillContainer) {
        super(skillContainer.getExecutor());
        this.skillContainer = skillContainer;
    }

    public SkillContainer getSkillContainer() {
        return this.skillContainer;
    }

    @Override
    public CounterAttackEvent post() {
       return EpicAPIEventHooks.Player.COUNTER_ATTACK.post(this);
    }
}
