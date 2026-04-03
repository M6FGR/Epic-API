package M6FGR.epic_api.api.cls;

import M6FGR.epic_api.api.exception.ClassLoadingException;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;

import java.util.Arrays;

import static M6FGR.epic_api.api.cls.LoadableInstance.*;

public interface ILoadableClass {
    static void loadClass(IEventBus bus, Class<? extends ILoadableClass> loadableClass) {
        if (!isClass(loadableClass)) {
            // make it not loaded, so it doesn't print out as forgotten!
            LOADED = false;
            LOGGER.error("Cannot load [{}]: not a class!", loadableClass.getName());
            return;
        }

        if (LOADED_CLASSES.contains(loadableClass)) {
            LOADED = false;
            throw new ClassLoadingException("Class [" + loadableClass.getName() + "] is already loaded!");
        }

        try {
            ILoadableClass loadableIns = loadableClass.getDeclaredConstructor().newInstance();
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
                LOADED = true;
                LOGGER.info("Loaded class: [{}]", loadableClass.getSimpleName());
            }
        } catch (NoSuchMethodException noMethodEx) {
            LOADED = false;
            LOGGER.error("Error loading Class [{}], It doesn't have a public constructor!", loadableClass.getName());
        } catch (Exception e) {
            LOADED = false;
            LOGGER.error("Error loading Class [{}], {}", loadableClass.getName(), e.getMessage());
        }
    }




    @SafeVarargs
    static void loadClasses(IEventBus bus, Class<? extends ILoadableClass>... loadableClasses) {
        if (loadableClasses.length == 1) {
            LOGGER.warn("Class [{}] is loaded via loadClasses() method, use loadClass() instead.", Arrays.stream(loadableClasses).iterator().next().getSimpleName());
        }
        for (Class<? extends ILoadableClass> cls : loadableClasses) {
            loadClass(bus, cls);
        }
        // first, we get the mod-id for the mod
        ModContainer container = ModLoadingContext.get().getActiveContainer();
        String autoModId = container.getModId();
        // then, we check what's forgotten here
        LoadableInstance.checkUnloaded(autoModId);
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
    default boolean shouldLoad() {
        return true;
    }
}