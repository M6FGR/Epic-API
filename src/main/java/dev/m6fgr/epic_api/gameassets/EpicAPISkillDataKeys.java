package dev.m6fgr.epic_api.gameassets;

import dev.m6fgr.epic_api.cls.loadable.ILoadableClass;
import dev.m6fgr.epic_api.main.EpicAPI;
import dev.m6fgr.epic_api.skills.common.CounterAttack;
import dev.m6fgr.epic_api.skills.common.HeavyAttack;
import dev.m6fgr.epic_api.utils.assets.DeferredRegistryUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillDataKey;

public class EpicAPISkillDataKeys implements ILoadableClass {
    private static final DeferredRegister<SkillDataKey<?>> SKILL_DATA_KEYS = DeferredRegistryUtil.createDefaultSDK();
    public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> HEAVY_COUNTER;
    public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> MOTION_COUNTER;

    static {
        HEAVY_COUNTER = register("heavy_counter" ,ByteBufCodecs.INT, 0, false, HeavyAttack.class);
        MOTION_COUNTER = register("motion_counter", ByteBufCodecs.INT, 0, false, CounterAttack.class);
    }

    @Override
    public void onModConstruct(IEventBus modBus) {
        SKILL_DATA_KEYS.register(modBus);
        EpicAPI.debug("Registered EpicAPI Skill Data Keys.");
    }

    @SafeVarargs
    private static <T> DeferredHolder<SkillDataKey<?>, SkillDataKey<T>> register(String name, StreamCodec<? super RegistryFriendlyByteBuf, T> packetCodec, T defaultValue, boolean syncronizeTrackingPlayers, Class<? extends Skill>... skillClass) {
        return SKILL_DATA_KEYS.register(name, () -> SkillDataKey.createSkillDataKey(packetCodec, defaultValue, syncronizeTrackingPlayers, skillClass));
    }
}
