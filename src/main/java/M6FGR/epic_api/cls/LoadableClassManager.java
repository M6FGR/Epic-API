package M6FGR.epic_api.cls;

import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.IModFileInfo;
import net.neoforged.neoforgespi.language.ModFileScanData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

final class LoadableClassManager {
    public static final Logger LOGGER = LogManager.getLogger("ILoadableClass");
    public static final List<Class<? extends ILoadableClass>> LOADED_CLASSES = new ArrayList<>();
    // to specify classes that had an error loading, we add a boolean as a firewall
    public static boolean LOADED = false;

    public static void checkUnloaded(String modId) {
        IModFileInfo modFile = ModList.get().getModFileById(modId);
        if (modFile == null) return;

        ModFileScanData scanData = modFile.getFile().getScanResult();
        Type iLoadableClassType = Type.getType(ILoadableClass.class);

        Set<String> loadedNames = LOADED_CLASSES.stream()
                .map(Class::getName)
                .collect(Collectors.toSet());

        scanData.getClasses().stream()
                .filter(data -> data.interfaces().contains(iLoadableClassType))
                .forEach(data -> {
                    String className = data.clazz().getClassName();

                    if (loadedNames.contains(className)) {
                        return; // Skip: We already loaded this one.
                    }

                    try {
                        // Used this forName() instead, it's safer and doesn't crash on a dedicated server
                        Class<?> cls = Class.forName(className, false, LoadableClassManager.class.getClassLoader());

                        if (ILoadableClass.class.isAssignableFrom(cls) && isClass(cls)) {
                            if (!loadedNames.contains(cls.getName()) && !LOADED) {
                                LOGGER.error("Class: [{}] is never loaded", className);
                            }
                        }
                    } catch (ClassNotFoundException | NoClassDefFoundError ignored) {
                        // NoClassDefFoundError is common on servers for client classes,
                        // catching it here prevents the crash.
                    }
                });
    }

    public static boolean isClass(Class<?> clazz) {
        return !clazz.isInterface() && !clazz.isEnum() && !clazz.isAnnotation();
    }
}
