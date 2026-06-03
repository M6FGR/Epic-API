package M6FGR.epic_api.events.player;

import M6FGR.epic_api.events.EpicAPIEventHooks;
import M6FGR.epic_api.events.IEventHook;
import yesman.epicfight.api.event.LivingEntityPatchEvent;
import yesman.epicfight.skill.SkillContainer;

public class HeavyAttackEvent extends LivingEntityPatchEvent implements IEventHook<HeavyAttackEvent> {
    private final SkillContainer container;
    public HeavyAttackEvent(SkillContainer skillContainer) {
        super(skillContainer.getExecutor());
        this.container = skillContainer;
    }

    public SkillContainer getSkillContainer() {
       return container;
    }

    @Override
    public HeavyAttackEvent post() {
       return EpicAPIEventHooks.Player.HEAVY_ATTACK.post(this);
    }
}
