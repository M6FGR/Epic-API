package M6FGR.epic_api.events.epic_api.player;

import M6FGR.epic_api.events.epic_api.EpicAPIEventHooks;
import M6FGR.epic_api.events.IEventHook;
import yesman.epicfight.api.event.CancelableEvent;
import yesman.epicfight.api.event.LivingEntityPatchEvent;
import yesman.epicfight.skill.SkillContainer;

public class HeavyAttackEvent extends LivingEntityPatchEvent implements IEventHook<HeavyAttackEvent>, CancelableEvent {
    private final SkillContainer skillContainer;

    public HeavyAttackEvent(SkillContainer skillContainer) {
        super(skillContainer.getExecutor());
        this.skillContainer = skillContainer;
    }

    public SkillContainer getSkillContainer() {
        return this.skillContainer;
    }

    @Override
    public HeavyAttackEvent post() {
       return EpicAPIEventHooks.Player.HEAVY_ATTACK.post(this);
    }
}
