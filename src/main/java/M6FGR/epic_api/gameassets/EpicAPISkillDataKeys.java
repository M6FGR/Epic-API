package M6FGR.epic_api.gameassets;

import M6FGR.epic_api.api.cls.ILoadableClass;
import M6FGR.epic_api.main.EpicAPI;
import M6FGR.epic_api.skills.common.HeavyAttack;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import yesman.epicfight.registry.EpicFightRegistries;
import yesman.epicfight.skill.SkillDataKey;

public class EpicAPISkillDataKeys implements ILoadableClass {
    public static final DeferredRegister<SkillDataKey<?>> SKILL_DATA_KEYS = DeferredRegister.create(EpicFightRegistries.SKILL_DATA_KEY, EpicAPI.MODID);
    public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> HEAVY_COUNTER;

    static {
        HEAVY_COUNTER = SKILL_DATA_KEYS.register("heavy_counter", () -> {
            return SkillDataKey.createSkillDataKey(ByteBufCodecs.INT, 0, false, HeavyAttack.class);
        });
    }

    @Override
    public void onModConstructor(IEventBus modBus) {
        SKILL_DATA_KEYS.register(modBus);
    }
}
