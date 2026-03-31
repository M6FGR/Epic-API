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


/**
 * This interface is used for ease of registering events, such as {@code IEventBus}, {@code IModEventBus}, and it's relations.
 * <p>
 * Example usage of the interface:
 * </p>
 * <blockquote><pre>
 * public class ExampleClass implements ILoadableClass {
 *
 *   private static void registerStaticEvent() {
 *       EpicFightEventHooks.Registry.EXAMPLE_EVENT.registerEvent(event -> {
 *      // ... the event
 *
 *   }
 *
 *   private void registerExampleEvent() {
 *         EpicFightEventHooks.Player.EXAMPLE_EVENT.registerEvent(event -> {
 *        // ... the event
 *
 *   }
 *
 *    {@code @Override}
 *    public void onModConstructor(IEventBus modBus) {
 *        modBus.<FMLCommonSetupEvent>addListener(commonEvent -> {
 *        commonEvent.enqueueWork(this::registerExampleEvent);
 *        commonEvent.enqueueWork(ExampleClass::registerStaticEvent);
 *       }
 *    }
 *    {@code @Override}
 *    public void onNeoForgeConstructor(IEventBus neoBus) {
 *        bus.addListener(this::registerItems);
 *    }
 *
 * }
 * </pre></blockquote>
 * <blockquote><pre>
 * public class MyMod {
 *     public MyMod(IEventBus modBus) {
 *         ILoadableClass.loadClass(modBus, ExampleClass.class)
 *         ILoadableClass.loadClasses(modBus, ExampleClass.class, AnotherClass.class...)
 *     }
 * }
 * </pre></blockquote>
 * And you can call {@code loadClass()} and {@code loadClasses()} methods in your main class, inside the constructor to use the mod's bus.
 *
 * @author M6FGR
 */

public interface ILoadableClass {
    static void loadClass(IEventBus bus, Class<? extends ILoadableClass> loadableClass) {
        if (!isClass(loadableClass)) {
            LOGGER.error("Cannot load [{}]: not a class!", loadableClass.getName());
            return;
        }

        if (LOADED_CLASSES.contains(loadableClass)) {
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
                    bus.addListener(loadableIns::onModClientEvents);
                    loadableIns.onNeoForgeClientConstructor(NeoForge.EVENT_BUS);
                } else {
                    bus.addListener(loadableIns::onModServerEvents);
                }

                LOADED_CLASSES.add(loadableClass);
                LOGGER.info("Loaded class: [{}]", loadableClass.getSimpleName());
            }
        } catch (NoSuchMethodException noMethodEx) {
            LOGGER.error("Error loading class [{}], It doesn't have a public constructor!", loadableClass.getName());
        } catch (Exception e) {
            LOGGER.error("Cannot load class [{}], {}", loadableClass.getName(), e.getMessage());
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
        ModContainer container = ModLoadingContext.get().getActiveContainer();
        String autoModId = container.getModId();
        // we check what's forgotten here
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
    default void onModCommonEvents(FMLCommonSetupEvent event) {}
    /** Use to register listeners to the FMLClientSetupEvent. Registered as a listener. */
    default void onModClientEvents(FMLClientSetupEvent event) {}
    /** Use to register listeners to the FMLDedicatedServerSetupEvent. Registered as a listener. */
    default void onModServerEvents(FMLDedicatedServerSetupEvent event) {}


    /** Use to load a class under specific Conditions. */
    default boolean shouldLoad() {
        return true;
    }
}