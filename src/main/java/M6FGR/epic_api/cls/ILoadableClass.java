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
import net.neoforged.neoforge.common.NeoForge;

import java.lang.reflect.Constructor;

import static M6FGR.epic_api.cls.LoadableClassManager.LOADED;
import static M6FGR.epic_api.cls.LoadableClassManager.LOADED_CLASSES;
import static M6FGR.epic_api.cls.LoadableClassManager.LOGGER;

public interface ILoadableClass {

    @SafeVarargs
    static void loadClass(IEventBus bus, Class<? extends ILoadableClass>... loadableClasses) {
        for (Class<? extends ILoadableClass> loadableClass : loadableClasses) {
            if (!LoadableClassManager.isClass(loadableClass)) {
                LOGGER.error("Cannot load [{}]: not a class!", loadableClass.getName());
                continue;
            }

            if (LOADED_CLASSES.contains(loadableClass)) {
                throw new ClassLoadingException("Class [" + loadableClass.getName() + "] is already loaded!");
            }

            try {
                Compatibility compatibilityAnn = loadableClass.getAnnotation(Compatibility.class);
                String simpleClassName = loadableClass.getSimpleName();
                StringBuilder modDisplayNames = new StringBuilder();
                Constructor<? extends ILoadableClass> constructor = loadableClass.getDeclaredConstructor();
                constructor.setAccessible(true);
                ILoadableClass instance = constructor.newInstance();

                // Compatibility verification (Only run if the annotation exists)
                if (compatibilityAnn != null) {
                    // Compatibility Side Check
                    if (compatibilityAnn.clientSide() && EpicAPI.getEnvHelper().isClient()) {
                        if (compatibilityAnn.debug()) {
                            LOGGER.debug("Skipping Client-Only Compatibility Class [{}]: On Dedicated Server.", simpleClassName);
                        }
                        continue;
                    }

                    // Mod Presence Check
                    String[] requiredMods = compatibilityAnn.modid();
                    boolean allModsLoaded = true;

                    for (String modid : requiredMods) {
                        if (!ModList.get().isLoaded(modid)) {
                            allModsLoaded = false;
                            if (compatibilityAnn.debug()) {
                                LOGGER.info("Compatibility class [{}] skipped: Missing mod '{}'.", simpleClassName, modid);
                            }
                            break;
                        }

                        modDisplayNames.append(modDisplayNames.isEmpty() ? "" : ", ")
                                .append(ModList.get().getModContainerById(modid)
                                        .map(c -> c.getModInfo().getDisplayName())
                                        .orElse(modid));
                    }

                    if (!allModsLoaded) continue;
                }


                instance.onModConstructor(bus);
                instance.onNeoForgeConstructor(NeoForge.EVENT_BUS);
                bus.addListener(instance::onModCommonEvents);

                 if (EpicAPI.getEnvHelper().isClient()) {
                    instance.onModClientConstructor(bus);
                    instance.onNeoForgeClientConstructor(NeoForge.EVENT_BUS);
                    bus.addListener(instance::onModClientEvents);
                } else {
                    bus.addListener(instance::onModServerEvents);
                }


                // Informative logging
                if (loadableClass.isAnnotationPresent(Compatibility.class)) {
                    LOGGER.info("Loaded Compatibility Class [{}] for ({})", simpleClassName, modDisplayNames);
                } else {
                    LOGGER.info("Loaded Class: [{}]", simpleClassName);
                }

                LOADED_CLASSES.add(loadableClass);
                LOADED = true;

            } catch (NoSuchMethodException noCons) {
                EpicAPI.err("Failed to load Class [{}], no public constructor!", loadableClass.getSimpleName());
                noCons.printStackTrace();

            } catch (Exception e) {
                EpicAPI.err("Failed to load Class [{}], {}", loadableClass.getName(), e);
                e.printStackTrace();
            }
        }

        // set loaded here

        // Auto-namespace check
        ModContainer container = ModLoadingContext.get().getActiveContainer();
        LoadableClassManager.checkUnloaded(container.getModId());
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

}