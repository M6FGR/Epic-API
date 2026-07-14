package M6FGR.epic_api.main;

import M6FGR.epic_api.cls.Comment;
import M6FGR.epic_api.cls.ILoadableClass;
import M6FGR.epic_api.events.epic_api.EpicAPIEventHooks;
import M6FGR.epic_api.events.epic_api.registry.EntityPatchEventHook;
import M6FGR.epic_api.events.mc.MinecraftEventHooks;
import M6FGR.epic_api.gameassets.EpicAPIKeyMappings;
import M6FGR.epic_api.gameassets.EpicAPISkillDataKeys;
import M6FGR.epic_api.gameassets.EpicAPISkills;
import M6FGR.epic_api.input.EpicAPIIntputAction;
import M6FGR.epic_api.network.EpicAPINetworkManager;
import M6FGR.epic_api.skills.EpicAPISkillCategories;
import M6FGR.epic_api.skills.EpicAPISkillSlots;
import M6FGR.epic_api.utils.EnvironmentHelper;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yesman.epicfight.api.client.input.action.InputAction;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SkillSlot;

@Mod(EpicAPI.MOD_ID)
public class EpicAPI {
    public static final String MOD_ID = "epic_api";
    public static final String MOD_NAME = "EpicAPI";
    private static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    public EpicAPI(IEventBus modBus) {
        modBus.addListener(this::modConstruct);
        ILoadableClass.loadClass(modBus,
                EpicAPINetworkManager.class,
                EpicAPISkills.class,
                EpicAPISkillDataKeys.class,
                EpicAPIKeyMappings.class
        );
        // this.debugs(modBus);
        // EpicFight Extensible Enums Registry
        SkillSlot.ENUM_MANAGER.registerEnumCls(MOD_ID, EpicAPISkillSlots.class);
        SkillCategory.ENUM_MANAGER.registerEnumCls(MOD_ID, EpicAPISkillCategories.class);
        InputAction.ENUM_MANAGER.registerEnumCls(MOD_ID, EpicAPIIntputAction.class);
    }


    @Comment("For debugging events and such, even this annotation")
    private void debugs(IEventBus modBus) {
        if (!EnvironmentHelper.getCurrentEnvironment().isDevEnv()) return;
       // Minecraft EventHooks
       MinecraftEventHooks.Player.TICK_PRE.registerEvent(event -> debugIfDevSide("Hello from PlayerTickEventHook#Pre!"));
       MinecraftEventHooks.Player.TICK_POST.registerEvent(event -> debugIfDevSide("Hello from PlayerTickEventHook#Post!"));

       MinecraftEventHooks.Player.JOIN_CLIENT.registerEvent(event -> {
           if (!event.getLevel().isClientSide()) return;
           debugIfDevSide("Hello from PlayerJoinEventHook#Client!");
       });
       MinecraftEventHooks.Player.JOIN_SERVER.registerEvent(event -> {
           if (event.getLevel().isClientSide()) return;
           debugIfDevSide("Hello from PlayerJoinEventHook#Server!");
       });

       MinecraftEventHooks.Server.SERVER_START_POST.registerEvent(event -> debugIfDevSide("Hello from ServerStartEventHook#Post!"));
       MinecraftEventHooks.Server.SERVER_START_PRE.registerEvent(event -> debugIfDevSide("Hello from ServerStartEventHook#Pre!"));
       MinecraftEventHooks.Server.SERVER_TICK.registerEvent(event -> debugIfDevSide("Hello from ServerTickEventHook!"));
       MinecraftEventHooks.Server.SERVER_STOP.registerEvent(event -> debugIfDevSide("Hello from ServerStopEventHook!"));

       MinecraftEventHooks.Client.WORLD_CREATE.registerEvent(event -> debugIfDevSide("Hello from WorldLoadEventHook#Create!"));
       MinecraftEventHooks.Client.WORLD_JOIN.registerEvent(event -> debugIfDevSide("Hello from WorldLoadEventHook#Join!"));
       MinecraftEventHooks.Client.WORLD_INIT.registerEvent(event -> debugIfDevSide("Hello from WorldLoadEvent#Initialize!"));

       // EpicAPI EventHooks
       EpicAPIEventHooks.Registry.ENTITY_PATCH.registerEvent(event -> debugIfDevSide("Hello from EntityPatchEventHook!"));
       EpicAPIEventHooks.Player.COUNTER_ATTACK.registerEvent(event -> debugIfDevSide("Hello from CounterAttackEventHook!"));
       EpicAPIEventHooks.Player.HEAVY_ATTACK.registerEvent(event -> debugIfDevSide("Hello from HeavyAttackEventHook!"));
    }

    // Logger helpers
    public static void err(String message, Object... args) {
        LOGGER.error(message, args);
    }

    public static void errIfDevSide(String message, Object... args) {
        if (EnvironmentHelper.getCurrentEnvironment().isDevEnv()) LOGGER.error(message, args);
    }

    public static void warn(String message, Object... args) {
        LOGGER.warn(message, args);
    }

    public static void debug(String message, Object... args) {
        LOGGER.debug(message, args);
    }

    public static void debugIfDevSide(String message, Object... args) {
        if (EnvironmentHelper.getCurrentEnvironment().isDevEnv()) LOGGER.debug(message, args);
    }

    public static void info(String message, Object... args) {
        LOGGER.debug(message, args);
    }


    // Resource locator
    public static ResourceLocation identifier(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }


    private void modConstruct(FMLConstructModEvent event) {
        EntityPatchEventHook entityPatchEH = new EntityPatchEventHook();
        event.enqueueWork(entityPatchEH::post);
    }


}
