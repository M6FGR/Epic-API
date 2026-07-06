package M6FGR.epic_api.gameassets;

import M6FGR.epic_api.cls.ILoadableClass;
import M6FGR.epic_api.main.EpicAPI;
import M6FGR.epic_api.skills.common.CounterAttack;
import M6FGR.epic_api.skills.common.HeavyAttack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import yesman.epicfight.api.utils.PacketBufferCodec;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.skill.SkillDataKey;

public class EpicAPISkillDataKeys implements ILoadableClass {
    private static final DeferredRegister<SkillDataKey<?>> SKILL_DATA_KEYS = DeferredRegister.create(EpicFightMod.identifier("skill_data_keys"), EpicAPI.MODID);
    public static final RegistryObject<SkillDataKey<Integer>> HEAVY_COUNTER;
    public static final RegistryObject<SkillDataKey<Integer>> COUNTER_MOTION;

    static {
        HEAVY_COUNTER = SKILL_DATA_KEYS.register("heavy_counter", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 0, false, HeavyAttack.class));
        COUNTER_MOTION = SKILL_DATA_KEYS.register("counter_motion", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 0, false, CounterAttack.class));
    }

    @Override
    public void onModConstructor(IEventBus modBus) {
        SKILL_DATA_KEYS.register(modBus);
        EpicAPI.debug("Registered EpicAPI Skill Data Keys.");
    }
}
