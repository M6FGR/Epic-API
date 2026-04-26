package M6FGR.epic_api.gameassets;

import M6FGR.epic_api.cls.ILoadableClass;
import M6FGR.epic_api.main.EpicAPI;
import M6FGR.epic_api.skills.common.HeavyAttack;
import net.minecraftforge.eventbus.api.IEventBus;
import yesman.epicfight.api.forgeevent.SkillBuildEvent;
import yesman.epicfight.skill.Skill;

public class EpicAPISkills implements ILoadableClass {
    public static Skill HEAVY_ATTACKS;


    private void onSkillRegistry(SkillBuildEvent event) {
        SkillBuildEvent.ModRegistryWorker registryWorker = event.createRegistryWorker(EpicAPI.MODID);
        registryWorker.build("heavy_attack", HeavyAttack::new, HeavyAttack.createHeavyAttackBuilder());
    }


    @Override
    public void onModConstructor(IEventBus bus) {
        bus.addListener(this::onSkillRegistry);
        EpicAPI.debug("Registered EpicAPI Skills.");
    }
}
