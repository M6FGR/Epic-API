package M6FGR.epic_api.main;

import M6FGR.epic_api.cls.ILoadableClass;
import M6FGR.epic_api.events.entity.EntityPatchBuilderRegistryEvent;
import M6FGR.epic_api.events.item.MoveSetBuilderRegistryEvent;
import M6FGR.epic_api.gameassets.EpicAPISkillDataKeys;
import M6FGR.epic_api.gameassets.EpicAPISkills;
import M6FGR.epic_api.input.EpicAPIIntputAction;
import M6FGR.epic_api.network.EpicAPINetworkManager;
import M6FGR.epic_api.skills.EpicAPISkillCategories;
import M6FGR.epic_api.skills.EpicAPISkillSlots;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yesman.epicfight.api.client.input.action.InputAction;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SkillSlot;

@Mod(EpicAPI.MODID)
public class EpicAPI {
    public static final String MODID = "epic_api";
    private static final Logger LOGGER = LogManager.getLogger("EpicAPI");

    public EpicAPI(FMLJavaModLoadingContext context) {
        IEventBus modBus = context.getModEventBus();

        ILoadableClass.loadClasses(modBus,
                // Assets registry
                EpicAPISkills.class,
                EpicAPISkillDataKeys.class,
                // Events
                EntityPatchBuilderRegistryEvent.class,
                MoveSetBuilderRegistryEvent.class,
                // Network
                EpicAPINetworkManager.class
        );


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

    public static void info(String message, Object... args) {
        LOGGER.debug(message, args);
    }

    private static boolean sideIs(Dist dist) {
        return FMLEnvironment.dist == dist;
    }

    public static boolean isClient() {
        return sideIs(Dist.CLIENT);
    }

    public static boolean isDedicatedServer() {
        return sideIs(Dist.DEDICATED_SERVER);
    }

    public static boolean isDeveloper() {
        return !FMLEnvironment.production;
    }

    public static ResourceLocation identifier(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    // removed IEventHook so, there is no commonEvents here

}
