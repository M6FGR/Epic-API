package M6FGR.epic_api.cls;

import M6FGR.epic_api.exception.ClassLoadingException;
import M6FGR.epic_api.main.EpicAPI;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static M6FGR.epic_api.cls.LoadableClassManager.*;



public interface ILoadableClass {
    static void loadClass(IEventBus bus, Class<? extends ILoadableClass> loadableClass) {
        if (!isClass(loadableClass)) {
            LOGGER.error("Cannot load [{}]: not a class!", loadableClass.getName());
            return;
        }

        if (LOADED_CLASSES.contains(loadableClass)) {
            throw new ClassLoadingException("Class [" + loadableClass.getName() + "] is already loaded!");
        }

        if (!hasAnyImplementation(loadableClass)) {
            throw new ClassLoadingException("Class [" + loadableClass.getName() + "] is unused! It does not override any hooks.");
        }

        try {
            Compatibility compatibilityAnn = loadableClass.getAnnotation(Compatibility.class);
            String simpleClassName = loadableClass.getSimpleName();

            if (compatibilityAnn != null) {
                String targetModId = compatibilityAnn.modid();
                String modDisplayName = ModList.get().getModContainerById(targetModId)
                        .map(container -> container.getModInfo().getDisplayName())
                        .orElse(targetModId);

                // 1. Side Check (Always exit early if we're on a server, and it's a client class)
                if (compatibilityAnn.clientSide() && FMLLoader.getDist().isDedicatedServer()) {
                    if (compatibilityAnn.printWarns()) {
                        LOGGER.debug("Skipping Client-Only Compatibility Class [{}]: on Dedicated Server.", simpleClassName);
                    }
                    return;
                }

                // 2. Presence Check (Always exit if the mod is missing)
                if (!ModList.get().isLoaded(targetModId)) {
                    if (compatibilityAnn.printWarns()) {
                        LOGGER.info("Compatibility class [{}] is skipped: Requires mod '{}' which is not present.",
                                simpleClassName, targetModId);
                    }
                    return;
                }

                LOGGER.info("Loaded Compatibility Class [{}] for ({})", simpleClassName, modDisplayName);
            }

            // Runs for both regular and verified compat classes
            Constructor<? extends ILoadableClass> constructor = loadableClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            ILoadableClass loadableIns = constructor.newInstance();
            if (loadableIns.shouldLoad()) {
                loadableIns.onModConstructor(bus);
                loadableIns.onNeoForgeConstructor(NeoForge.EVENT_BUS);
                bus.addListener(loadableIns::onModCommonEvents);

                if (FMLLoader.getDist().isClient()) {
                    loadableIns.onModClientConstructor(bus);
                    loadableIns.onNeoForgeClientConstructor(NeoForge.EVENT_BUS);
                    bus.addListener(loadableIns::onModClientEvents);
                } else {
                    bus.addListener(loadableIns::onModServerEvents);
                }
                LOADED_CLASSES.add(loadableClass);

                // Only print the generic "Loaded Class" log if it wasn't already handled by the Compat log above
                if (compatibilityAnn == null) {
                    LOGGER.info("Loaded Class: [{}]", simpleClassName);
                }
            } else {
                LOGGER.debug("Class [{}] was instantiated but shouldLoad() returned false.", simpleClassName);
            }
        } catch (Exception e) {
            EpicAPI.LOGGER.error("Failed to load Class [" + loadableClass.getName() + "]", e);
        } finally {
            LOADED = true;
        }
    }

    private static boolean hasAnyImplementation(Class<? extends ILoadableClass> cls) {
        String[] actionMethods = {
                "onModConstructor", "onModClientConstructor", "onNeoForgeConstructor",
                "onNeoForgeClientConstructor", "onModCommonEvents", "onModClientEvents",
                "onModServerEvents", "shouldLoad"
        };

        for (Method m : ILoadableClass.class.getDeclaredMethods()) {
            for (String target : actionMethods) {
                if (m.getName().equals(target)) {
                    try {
                        cls.getDeclaredMethod(m.getName(), m.getParameterTypes());
                        return true;
                    } catch (NoSuchMethodException ignored) {}
                }
            }
        }
        return false;
    }

    @SafeVarargs
    static void loadClasses(IEventBus bus, Class<? extends ILoadableClass>... loadableClasses) {
        if (loadableClasses.length == 1) {
            LOGGER.warn("Class [{}] is loaded via loadClasses() method, use loadClass() instead.", loadableClasses[0].getSimpleName());
        }
        for (Class<? extends ILoadableClass> cls : loadableClasses) {
            loadClass(bus, cls);
        }
        ModContainer container = ModLoadingContext.get().getActiveContainer();
        String autoModId = container.getModId();
        LoadableClassManager.checkUnloaded(autoModId);
    }

    /** Primary method to register Items, Blocks, Entities, etc... Called Directly. */
    default void onModConstructor(IEventBus modBus) {}

    /** Use to register Client-only listeners to the Mod Bus. Called Directly. */
    default void onModClientConstructor(IEventBus modBus) {}

    /** Use to register listeners to the global NeoForge.EVENT_BUS. Called Directly. */
    default void onNeoForgeConstructor(IEventBus neoBus) {}

    /** Use to register listeners to the client side of NeoForge.EVENT_BUS. Called Directly. */
    default void onNeoForgeClientConstructor(IEventBus neoBus) {}

    /** Use to register listeners to the FMLCommonSetupEvent. Registered as a listener. */
    default void onModCommonEvents(FMLCommonSetupEvent commonEvent) {}

    /** Use to register listeners to the FMLClientSetupEvent. Registered as a listener. */
    default void onModClientEvents(FMLClientSetupEvent clientEvent) {}

    /** Use to register listeners to the FMLDedicatedServerSetupEvent. Registered as a listener. */
    default void onModServerEvents(FMLDedicatedServerSetupEvent serverEvent) {}

    /** Use to load a class under specific Conditions. */
    default boolean shouldLoad() { return true; }
}