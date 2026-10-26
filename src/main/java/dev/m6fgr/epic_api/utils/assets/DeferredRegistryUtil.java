package dev.m6fgr.epic_api.utils.assets;

import dev.m6fgr.epic_api.main.EpicAPI;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus.Internal;
import yesman.epicfight.registry.EpicFightRegistries;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillDataKey;

public class DeferredRegistryUtil {

    public static <T> DeferredRegister<T> create(Registry<T> registry, String modID) {
       return DeferredRegister.create(registry, modID);
    }

    public static <T> DeferredRegister<T> create(ResourceKey<? extends Registry<T>> registry, String modID) {
       return DeferredRegister.create(registry, modID);
    }

    public static DeferredRegister<Item> createItems(String modID) {
        return DeferredRegister.createItems(modID);
    }

    public static DeferredRegister<Skill> createSkills(String modID) {
        return DeferredRegister.create(EpicFightRegistries.SKILL, modID);
    }

    public static DeferredRegister<SkillDataKey<?>> createSkillDataKeys(String modID) {
        return DeferredRegister.create(EpicFightRegistries.Keys.SKILL_DATA_KEY, modID);
    }

    @Internal
    public static DeferredRegister<SkillDataKey<?>> createDefaultSDK() {
        return DeferredRegister.create(EpicFightRegistries.Keys.SKILL_DATA_KEY, EpicAPI.MOD_ID);
    }

    @Internal
    public static DeferredRegister<Skill> createDefaultSkills() {
        return DeferredRegister.create(EpicFightRegistries.SKILL, EpicAPI.MOD_ID);
    }

}
