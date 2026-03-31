package M6FGR.epic_api.gameassets;

import M6FGR.epic_api.api.cls.ILoadableClass;
import M6FGR.epic_api.main.EpicAPI;
import M6FGR.epic_api.skills.common.HeavyAttack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import yesman.epicfight.registry.EpicFightRegistries;
import yesman.epicfight.skill.Skill;

public class EpicAPISkills implements ILoadableClass {
    private static final DeferredRegister<Skill> SKILLS;
    public static DeferredHolder<Skill, HeavyAttack> HEAVY_ATTACKS;

    static {
        SKILLS = DeferredRegister.create(EpicFightRegistries.Keys.SKILL, EpicAPI.MODID);

        HEAVY_ATTACKS = SKILLS.register("heavy_attack", key -> HeavyAttack.createHeavyAttackBuilder().build(key));
    }


    @Override
    public void onModConstructor(IEventBus bus) {
        SKILLS.register(bus);
    }
}
