package dev.m6fgr.epic_api.cls.loadable;

import dev.m6fgr.epic_api.cls.loadable.enums.LoadableEnum;
import dev.m6fgr.epic_api.cls.loadable.marks.Compatibility;
import dev.m6fgr.epic_api.cls.loadable.marks.Invoke;
import dev.m6fgr.epic_api.cls.maps.BasicHashMap;
import dev.m6fgr.epic_api.cls.maps.BasicMap;
import dev.m6fgr.epic_api.events.bus.ModBus;
import dev.m6fgr.epic_api.events.bus.NeoBus;
import dev.m6fgr.epic_api.exception.ClassLoadingException;
import dev.m6fgr.epic_api.main.EpicAPIMod;
import dev.m6fgr.epic_api.utils.EnvironmentHelper;
import dev.m6fgr.epic_api.utils.EnvironmentHelper.Environments;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforgespi.language.IModFileInfo;
import net.neoforged.neoforgespi.language.ModFileScanData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Type;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class LoadableClassManager {
    private static final Logger LOGGER = LogManager.getLogger("LoadableClassManager");
    private static final BasicMap<String, Class<? extends ILoadableClass>> LOADED_CLASSES = new BasicHashMap<>();
    private static final BasicMap<String, Class<? extends ILoadableClass>> ERRORED_CLASSES = new BasicHashMap<>();

    @SafeVarargs
    public static void loadClass(IEventBus bus, Class<? extends ILoadableClass>... loadableClasses) {
        for (Class<? extends ILoadableClass> loadableClass : loadableClasses) {
            if (!LoadableClassManager.acceptsImplementation(loadableClass)) {
                LOGGER.error("Cannot load [{}]: doesn't accept implementations!", loadableClass.getName());
                continue;
            }

            if (loadableClass.isEnum()) {
                LOGGER.warn("Class [{}] is an enum class!, use LoadableClassManager#loadEnum instead!", loadableClass.getSimpleName());
            }

            if (LOADED_CLASSES.containsValue(loadableClass)) {
                throw new ClassLoadingException("Class [" + loadableClass.getName() + "] is already loaded!");
            }

            Map<Class<?>, Object> allowedConstructorArgs = Map.of(
                    Environments.class, EnvironmentHelper.getEnvironment(),
                    Dist.class, FMLLoader.getDist(),
                    ModBus.class, bus,
                    NeoBus.class, NeoBus.get()
            );

            try {
                Compatibility compatibilityAnn = loadableClass.getAnnotation(Compatibility.class);
                String simpleClassName = loadableClass.getSimpleName();
                StringBuilder modDisplayNames = new StringBuilder();

                if (compatibilityAnn != null) {
                    if (compatibilityAnn.clientSide() && !EnvironmentHelper.getEnvironment().is(Environments.CLIENT)) {
                        if (compatibilityAnn.debug()) {
                            LOGGER.debug("Skipping Client-Only Compatibility Class [{}]: On Dedicated Server.", simpleClassName);
                        }
                        // Mark as handled/loaded so checkUnloaded doesn't flag it
                        LOADED_CLASSES.put(loadableClass.getSimpleName(), loadableClass);
                        continue;
                    }

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

                    if (!allModsLoaded) {
                        LOADED_CLASSES.put(loadableClass.getSimpleName(), loadableClass);
                        continue;
                    }
                }

                Constructor<?> selectedConstructor = null;
                Object[] resolvedArgs = null;
                Constructor<?> fallbackConstructor = null;
                Object[] fallbackArgs = null;

                for (Constructor<?> constructor : loadableClass.getDeclaredConstructors()) {
                    Class<?>[] parameterTypes = constructor.getParameterTypes();
                    Object[] args = new Object[parameterTypes.length];
                    boolean canResolve = true;

                    for (int i = 0; i < parameterTypes.length; i++) {
                        if (allowedConstructorArgs.containsKey(parameterTypes[i])) {
                            args[i] = allowedConstructorArgs.get(parameterTypes[i]);
                        } else {
                            canResolve = false;
                            break;
                        }
                    }

                    if (canResolve) {
                        if (constructor.isAnnotationPresent(Invoke.class)) {
                            selectedConstructor = constructor;
                            resolvedArgs = args;
                            break;
                        } else if (fallbackConstructor == null) {
                            fallbackConstructor = constructor;
                            fallbackArgs = args;
                        }
                    }
                }

                if (selectedConstructor == null) {
                    selectedConstructor = fallbackConstructor;
                    resolvedArgs = fallbackArgs;
                }

                if (selectedConstructor == null) {
                    LOGGER.error("Failed to load Class [{}]: No suitable constructor found with resolvable parameters.", simpleClassName);
                    continue;
                }

                selectedConstructor.setAccessible(true);
                ILoadableClass instance = (ILoadableClass) selectedConstructor.newInstance(resolvedArgs);

                instance.onModConstruct(bus);
                instance.onGameConstruct(NeoForge.EVENT_BUS);

                if (EnvironmentHelper.getEnvironment().is(Environments.CLIENT)) {
                    instance.onModClientConstruct(bus);
                    instance.onGameClientConstruct(NeoForge.EVENT_BUS);
                }

                if (selectedConstructor.isAnnotationPresent(Invoke.class)) {
                    LOGGER.info("Constructed class [{}]", simpleClassName);
                } else if (compatibilityAnn != null) {
                    LOGGER.info("Loaded compatibility class [{}] for ({})", simpleClassName, modDisplayNames);
                } else {
                    LOGGER.info("Loaded class: [{}]", simpleClassName);
                }

                ERRORED_CLASSES.removeIfPresent(loadableClass.getSimpleName(), loadableClass);
                LOADED_CLASSES.put(loadableClass.getSimpleName(), loadableClass);
            } catch (Exception e) {
                EpicAPIMod.err("Failed to load class [{}], {}", loadableClass.getName(), e);
                ERRORED_CLASSES.put(loadableClass.getSimpleName(), loadableClass);
                e.printStackTrace();
            }
        }
    }

    public static void runUnloadedCheck() {
        ModContainer container = ModLoadingContext.get().getActiveContainer();
        if (container != null) {
            checkUnloaded(container.getModId());
        }
    }

    public static Class<? extends ILoadableClass> getLoadedClassByName(String clsName) {
        return LOADED_CLASSES.get(clsName);
    }

    public static boolean hadErrorsLoading(Class<? extends ILoadableClass> loadableCls) {
        return ERRORED_CLASSES.containsValue(loadableCls);
    }

    private static void checkUnloaded(String modId) {
        IModFileInfo modFile = ModList.get().getModFileById(modId);
        if (modFile == null)
            return;

        ModFileScanData scanData = modFile.getFile().getScanResult();
        Type loadableClsType = Type.getType(ILoadableClass.class);

        Set<String> loadedNames = LOADED_CLASSES.values().stream()
                .map(Class::getName)
                .collect(Collectors.toSet());

        Set<String> errorClassNames = ERRORED_CLASSES.values().stream()
                .map(Class::getName)
                .collect(Collectors.toSet());

        scanData.getClasses().stream()
                .filter(data -> data.interfaces().contains(loadableClsType))
                .map(data -> data.clazz().getClassName())
                .filter(className -> !loadedNames.contains(className) && !errorClassNames.contains(className))
                .filter(className -> {
                    try {
                        Class<?> clazz = Class.forName(className, false, Thread.currentThread().getContextClassLoader());

                        Compatibility compatibilityAnn = clazz.getAnnotation(Compatibility.class);
                        if (compatibilityAnn != null) {
                            if (compatibilityAnn.clientSide() && !EnvironmentHelper.getEnvironment().is(Environments.CLIENT)) {
                                return false;
                            }
                            for (String modid : compatibilityAnn.modid()) {
                                if (!ModList.get().isLoaded(modid)) {
                                    return false;
                                }
                            }
                        }

                        if (ILoadableClass.class.isAssignableFrom(clazz)) {
                            try {
                                var constructor = clazz.getDeclaredConstructor();
                                constructor.setAccessible(true);
                                ILoadableClass instance = (ILoadableClass) constructor.newInstance();
                                return !instance.ignoreNotLoadedWarning();
                            } catch (NoSuchMethodException e) {
                                var method = clazz.getMethod("ignoreNotLoadedWarning");
                                method.setAccessible(true);
                                if (Modifier.isStatic(method.getModifiers())) {
                                    return !((Boolean) method.invoke(null));
                                }
                            }
                        }
                    } catch (Throwable t) {
                        return false;
                    }
                    return true;
                })
                .forEach(className -> {
                    if (!loadedNames.contains(className) && !errorClassNames.contains(className)) {
                        if (EnvironmentHelper.getEnvironment().isDevEnv()) {
                            throw new ClassLoadingException("Class [" + className + "] is never loaded!");
                        } else {
                            EpicAPIMod.err("Class [{}] is never loaded!, report this issue asap!", className);
                        }
                    }
                });
    }

    public static boolean acceptsImplementation(Class<?> clazz) {
        return !clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers());
    }

    public static void loadEnum(IEventBus modBus, LoadableEnum[] loadableEnumValues) {
        Class<? extends ILoadableClass>[] commonClasses = Arrays.stream(loadableEnumValues)
                .filter(loadableEnum -> !loadableEnum.isClientOnly())
                .map(LoadableEnum::getLoadableClass)
                .<Class<? extends ILoadableClass>>toArray(Class[]::new);

        loadClass(modBus, commonClasses);

        if (EnvironmentHelper.getEnvironment().isClient()) {
            Class<? extends ILoadableClass>[] clientClasses = Arrays.stream(loadableEnumValues)
                    .filter(LoadableEnum::isClientOnly)
                    .map(LoadableEnum::getLoadableClass)
                    .<Class<? extends ILoadableClass>>toArray(Class[]::new);

            loadClass(modBus, clientClasses);
        }

        // Run the unloaded check ONCE after all loads (both common and client) have completed
        runUnloadedCheck();
    }
}