package M6FGR.epic_api.main;

import M6FGR.epic_api.cls.ILoadableClass;
import M6FGR.epic_api.events.entity.EntityPatchBuilderRegistryEvent;
import M6FGR.epic_api.gameassets.EpicAPIKeyMappings;
import M6FGR.epic_api.gameassets.EpicAPISkillDataKeys;
import M6FGR.epic_api.gameassets.EpicAPISkills;
import M6FGR.epic_api.input.EpicAPIIntputAction;
import M6FGR.epic_api.network.EpicAPINetworkManager;
import M6FGR.epic_api.skills.EpicAPISkillCategories;
import M6FGR.epic_api.skills.EpicAPISkillSlots;
import M6FGR.epic_api.utils.EnvironmentHelper;
import M6FGR.epic_api.utils.EnvironmentHelper.Environments;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yesman.epicfight.api.client.forgeevent.PatchedRenderersEvent;
import yesman.epicfight.api.client.input.action.InputAction;
import yesman.epicfight.api.forgeevent.EntityPatchRegistryEvent;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SkillSlot;

@Mod(EpicAPI.MODID)
public class EpicAPI {
    public static final String MODID = "epic_api";
    private static final Logger LOGGER = LogManager.getLogger("EpicAPI");
    private static final EntityPatchBuilderRegistryEvent entityPatchEvent = new EntityPatchBuilderRegistryEvent();
    public EpicAPI(FMLJavaModLoadingContext context) {
        IEventBus modBus = context.getModEventBus();
        ILoadableClass.loadClasses(modBus,
                EpicAPISkills.class,
                EpicAPISkillDataKeys.class,
                EpicAPIKeyMappings.class,
                EpicAPINetworkManager.class
        );

        modBus.addListener(this::onEntityPatchRegistry);
        // EpicFight Extensible Enums Registry
        SkillSlot.ENUM_MANAGER.registerEnumCls(MODID, EpicAPISkillSlots.class);
        SkillCategory.ENUM_MANAGER.registerEnumCls(MODID, EpicAPISkillCategories.class);
        InputAction.ENUM_MANAGER.registerEnumCls(MODID, EpicAPIIntputAction.class);
    }

    // Logger helpers
    public static void err(String message, Object... args) {
        LOGGER.error(message, args);
    }

    public static void warn(String message, Object... args) {
        LOGGER.warn(message, args);
    }

    public static void debug(String message, Object... args) {
        LOGGER.debug(message, args);
    }

    public static void debugIfDevSide(String message, Object... args) {
        if (EnvironmentHelper.getInstance().getCurrentEnvironment().isDevEnv())
            debug(message, args);
    }


    public void onEntityPatchRegistry(EntityPatchRegistryEvent event) {
        entityPatchEvent.onEntityPatchRegistry(event);
    }

    public static void errIfDevSide(String message, Object... args) {
        if (EnvironmentHelper.getInstance().getCurrentEnvironment().isDevEnv())
            err(message, args);
    }

    public static void info(String message, Object... args) {
        LOGGER.debug(message, args);
    }

    public static ResourceLocation identifier(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
   public static class ClientSideEvents {
        @SubscribeEvent
        public static void onAddRenderers(PatchedRenderersEvent.Add event) {
            entityPatchEvent.onPatchedRenderers(event);
        }
   }

}
