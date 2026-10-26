package dev.m6fgr.epic_api.main;

import dev.m6fgr.epic_api.cls.Comment;
import dev.m6fgr.epic_api.cls.loadable.LoadableClassManager;
import dev.m6fgr.epic_api.cls.loadable.enums.EpicAPILoadableEnums;
import dev.m6fgr.epic_api.compat.EpicAPIPlugin;
import dev.m6fgr.epic_api.compat.plugin.CompatibilityPluginsManager;
import dev.m6fgr.epic_api.config.EpicAPIConfigs;
import dev.m6fgr.epic_api.events.dispatch.IDispatchableEvent;
import dev.m6fgr.epic_api.events.dispatch.abstracts.DispatchableEvent;
import dev.m6fgr.epic_api.events.dispatch.dispatchers.EventDispatch;
import dev.m6fgr.epic_api.events.dispatch.dispatchers.extras.EventPriority;
import dev.m6fgr.epic_api.events.epic_api.EpicAPIDispatchableEvents;
import dev.m6fgr.epic_api.events.epic_api.dispatch.DispatchEvent;
import dev.m6fgr.epic_api.events.epic_api.registry.EntityPatchDispatchableEvent;
import dev.m6fgr.epic_api.events.mc.MinecraftDispatchableEvents;
import dev.m6fgr.epic_api.gameassets.EpicAPIGameRules;
import dev.m6fgr.epic_api.input.EpicAPIIntputAction;
import dev.m6fgr.epic_api.skills.EpicAPISkillCategories;
import dev.m6fgr.epic_api.skills.EpicAPISkillSlots;
import dev.m6fgr.epic_api.utils.EnvironmentHelper;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;
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
    private final ModContainer container;
    private final IEventBus modBus;
    private static EpicAPI instance;

    public EpicAPI(IEventBus modBus, ModContainer container) {
        instance = this;
        this.modBus = modBus;
        this.container = container;
        this.doVanillaStuff();
        this.doEpicAPIStuff();
    }

    public static EpicAPI getInstance() {
        return instance;
    }

    private void modConstruct(FMLConstructModEvent event) {
        EntityPatchDispatchableEvent entityPatchEH = new EntityPatchDispatchableEvent();
        event.enqueueWork(entityPatchEH::post);
    }

    private void doVanillaStuff() {
        EpicAPIGameRules.BOOLEAN_GAMERULE.register();
    }

    private void doEpicAPIStuff() {
        IDispatchableEvent.dispatchEvents(this::events);
        DispatchEvent.postEvent();

        this.modBus.addListener(this::modConstruct);
        this.container.registerConfig(Type.COMMON, EpicAPIConfigs.Common.SPEC);

        CompatibilityPluginsManager.registerPlugins(EpicAPIPlugin.class);
        LoadableClassManager.loadEnum(this.modBus, EpicAPILoadableEnums.values());

        SkillSlot.ENUM_MANAGER.registerEnumCls(MOD_ID, EpicAPISkillSlots.class);
        SkillCategory.ENUM_MANAGER.registerEnumCls(MOD_ID, EpicAPISkillCategories.class);
        InputAction.ENUM_MANAGER.registerEnumCls(MOD_ID, EpicAPIIntputAction.class);
    }

    public ModContainer getContainer() {
        return this.container;
    }

    public IEventBus getModBus() {
        return this.modBus;
    }

    @Comment("For debugging events and such, even this annotation")
    private void events() {
        if (EnvironmentHelper.isDevelopmentEnvironment()) {
            MinecraftDispatchableEvents.getEventsAsList().forEach(this::debugEvent);
            EpicAPIDispatchableEvents.getEventsAsList().forEach(this::debugEvent);
        }
    }

    public static void debugIfDevSide(String message, Object... args) {
        if (EnvironmentHelper.getEnvironment().isDevEnv()) {
            LOGGER.debug(message, args);
        }
    }

    public <T extends EventDispatch<? extends DispatchableEvent>> void debugEvent(T eventDispatch) {
        eventDispatch.dispatchEvent(event -> {
            if (event.getPriority().is(EventPriority.MIDDLE)) {
                debugIfDevSide("Hello from {}!", event.getEventClsName());
            } else {
                debugIfDevSide("Hello from {}! with {} priority", event.getEventClsName(), event.getPriority());
            }
        });
    }

    public ResourceLocation identifier(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    // Logger helpers
    public static void err(String message, Object... args) {
        LOGGER.error(message, args);
    }

    public static void errIfDevSide(String message, Object... args) {
        if (EnvironmentHelper.getEnvironment().isDevEnv()) {
            LOGGER.error(message, args);
        }
    }

    public static void warn(String message, Object... args) {
        LOGGER.warn(message, args);
    }

    public static void debug(String message, Object... args) {
        LOGGER.debug(message, args);
    }


    public static void info(String message, Object... args) {
        LOGGER.info(message, args);
    }
}
