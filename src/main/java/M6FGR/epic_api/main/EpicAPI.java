package M6FGR.epic_api.main;

import M6FGR.epic_api.cls.ILoadableClass;
import M6FGR.epic_api.events.registry.EntityPatchEventHook;
import M6FGR.epic_api.gameassets.EpicAPIKeyMappings;
import M6FGR.epic_api.gameassets.EpicAPISkillDataKeys;
import M6FGR.epic_api.gameassets.EpicAPISkills;
import M6FGR.epic_api.input.EpicAPIIntputAction;
import M6FGR.epic_api.network.EpicAPINetworkManager;
import M6FGR.epic_api.skills.EpicAPISkillCategories;
import M6FGR.epic_api.skills.EpicAPISkillSlots;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yesman.epicfight.api.client.input.action.InputAction;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SkillSlot;

@Mod(EpicAPI.MODID)
public class EpicAPI {
    public static final String MODID = "epic_api";
    private static final Logger LOGGER = LogManager.getLogger("EpicAPI");
    private static final EnvironmentHelper environmentHelper = new EnvironmentHelper();
    private static boolean fact;

    public EpicAPI(IEventBus modBus) {
        modBus.addListener(this::constructMod);
        ILoadableClass.loadClass(modBus,
                EpicAPINetworkManager.class,
                EpicAPISkills.class,
                EpicAPISkillDataKeys.class,
                EpicAPIKeyMappings.class
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

    public static void errIfDevSide(String message, Object... args) {
        if (environmentHelper.isDevEnv()) LOGGER.error(message, args);
    }

    public static void warn(String message, Object... args) {
        LOGGER.warn(message, args);
    }

    public static void debug(String message, Object... args) {
        LOGGER.debug(message, args);
    }

    public static void debugIfDevSide(String message, Object... args) {
        if (environmentHelper.isDevEnv()) LOGGER.debug(message, args);
    }

    public static void info(String message, Object... args) {
        LOGGER.debug(message, args);
    }

    // Environment helper instance
    public static EnvironmentHelper getEnvHelper() {
        return environmentHelper;
    }

    // Resource locator
    public static ResourceLocation identifier(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }


    // Events
    private void constructMod(FMLConstructModEvent event) {
        // these events are fired in the common setup event, so we can fire it here?
        EntityPatchEventHook entityPatchEH = new EntityPatchEventHook();
        event.enqueueWork(entityPatchEH::post);
    }


    public static class EnvironmentHelper {
        // Packaged-visible constructor allows the outer class to instantiate it safely
        private EnvironmentHelper() {}

        private final Dist dist = FMLLoader.getDist();

        public boolean isClient() {
            return this.dist == Dist.CLIENT;
        }

        public boolean isDedicatedServer() {
            return this.dist == Dist.DEDICATED_SERVER;
        }

        public boolean isDevEnv() {
            return !FMLEnvironment.production;
        }

        public boolean isOfficialMC() {
            // if we run Minecraft.getInstance() on a server, it'd crash instantly, so we do a guard as so:
            if (!this.isClient()) return false;
            Minecraft mc = Minecraft.getInstance();
            User user = mc.getUser();
            return !user.getAccessToken().equals("0");
        }

        // yes, it's very possible, by adding the property (devLogin = true) in your build.gradle file
        public boolean isDevAndOfficialMC() {
            return this.isDevEnv() && this.isOfficialMC();
        }

        public boolean isServerOffline() {
            // same thing as above, it has a chance to crash if it was on the client-side
            if (!this.isDedicatedServer()) return false;
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server instanceof DedicatedServer dedicatedServer) {
                return !dedicatedServer.usesAuthentication();
            }
            return false;
        }

    }


}
