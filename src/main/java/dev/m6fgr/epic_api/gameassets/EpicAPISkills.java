package dev.m6fgr.epic_api.gameassets;

import dev.m6fgr.epic_api.cls.loadable.ILoadableClass;
import dev.m6fgr.epic_api.main.EpicAPI;
import dev.m6fgr.epic_api.skills.common.CounterAttack;
import dev.m6fgr.epic_api.skills.common.HeavyAttack;
import dev.m6fgr.epic_api.utils.assets.DeferredRegistryUtil;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillBuilder;

import java.util.function.Supplier;

public class EpicAPISkills implements ILoadableClass {
    private static final DeferredRegister<Skill> SKILLS = DeferredRegistryUtil.createDefaultSkills();

    public static final DeferredHolder<Skill, HeavyAttack> HEAVY_ATTACKS = register("heavy_attack", HeavyAttack::createHeavyAttackBuilder);

    public static final DeferredHolder<Skill, CounterAttack> COUNTER_ATTACK = register("counter_attack", CounterAttack::createCounterAttackBuilder);

    @Override
    public void onModConstruct(IEventBus bus) {
        SKILLS.register(bus);
        EpicAPI.debug("Registered EpicAPI Skills.");
    }

    private static <T extends SkillBuilder<?>, S extends Skill> DeferredHolder<Skill, S> register(String name, Supplier<T> supplier) {
        return SKILLS.register(name, key -> supplier.get().build(key));
    }
}
