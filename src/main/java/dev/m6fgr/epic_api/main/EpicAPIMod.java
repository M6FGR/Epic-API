package dev.m6fgr.epic_api.main;

import dev.m6fgr.epic_api.cls.loadable.LoadableClassManager;
import dev.m6fgr.epic_api.cls.loadable.enums.EpicAPILoadableEnums;
import dev.m6fgr.epic_api.config.EpicAPIConfigs;
import dev.m6fgr.epic_api.events.bus.ModBus;
import dev.m6fgr.epic_api.events.bus.NeoBus;
import dev.m6fgr.epic_api.events.dispatch.abstracts.DispatchableEvent;
import dev.m6fgr.epic_api.events.dispatch.dispatchers.EventDispatch;
import dev.m6fgr.epic_api.events.dispatch.dispatchers.extras.EventPriority;
import dev.m6fgr.epic_api.events.epic_api.dispatch.DispatchEvent;
import dev.m6fgr.epic_api.events.epic_api.registry.EntityPatchDispatchableEvent;
import dev.m6fgr.epic_api.gameassets.EpicAPIGameRules;
import dev.m6fgr.epic_api.input.EpicAPIIntputAction;
import dev.m6fgr.epic_api.skills.EpicAPISkillCategories;
import dev.m6fgr.epic_api.skills.EpicAPISkillSlots;
import dev.m6fgr.epic_api.utils.EnvironmentHelper;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import yesman.epicfight.api.client.input.action.InputAction;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SkillSlot;

@Mod(EpicAPI.MOD_ID)
public final class EpicAPIMod implements EpicAPI {
    private final ModContainer container;
    private final ModBus modBus;
    static EpicAPIMod instance;

    public EpicAPIMod(IEventBus modBus, ModContainer container) {
        instance = this;
        this.modBus = ModBus.from(modBus);
        this.container = container;
        this.doEpicAPIStuff();
    }

    private void modConstruct(FMLConstructModEvent event) {
        EntityPatchDispatchableEvent entityPatchEH = new EntityPatchDispatchableEvent();
        event.enqueueWork(entityPatchEH::post);
    }

    private void doEpicAPIStuff() {
        DispatchEvent.postEvent();

        this.modBus.addListener(this::modConstruct);

        EpicAPIConfigs.register(this.container);
        EpicAPIGameRules.BOOLEAN_GAMERULE.register();

        LoadableClassManager.loadEnum(this.modBus, EpicAPILoadableEnums.values());

        SkillSlot.ENUM_MANAGER.registerEnumCls(EpicAPI.MOD_ID, EpicAPISkillSlots.class);
        SkillCategory.ENUM_MANAGER.registerEnumCls(EpicAPI.MOD_ID, EpicAPISkillCategories.class);
        InputAction.ENUM_MANAGER.registerEnumCls(EpicAPI.MOD_ID, EpicAPIIntputAction.class);
    }

    @Override
    public boolean isDeveloperEnvironment() {
        return EnvironmentHelper.isDevelopmentEnvironment();
    }

    public ModContainer getModContainer() {
        return this.container;
    }

    public ModBus getModBus() {
        return this.modBus;
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
        return ResourceLocation.fromNamespaceAndPath(EpicAPI.MOD_ID, path);
    }

    @Override
    public NeoBus getNeoBus() {
        return NeoBus.get();
    }

    // Logger helpers
    public static void err(String message, Object... args) {
        EpicAPI.LOGGER.error(message, args);
    }

    public static void errIfDevSide(String message, Object... args) {
        if (EnvironmentHelper.getEnvironment().isDevEnv()) {
            EpicAPI.LOGGER.error(message, args);
        }
    }

    public static void warn(String message, Object... args) {
        EpicAPI.LOGGER.warn(message, args);
    }

    public static void debug(String message, Object... args) {
        EpicAPI.LOGGER.debug(message, args);
    }

    public static void debugIfDevSide(String message, Object... args) {
        if (EnvironmentHelper.getEnvironment().isDevEnv()) {
            EpicAPI.LOGGER.debug(message, args);
        }
    }

    public static void info(String message, Object... args) {
        EpicAPI.LOGGER.info(message, args);
    }
}
