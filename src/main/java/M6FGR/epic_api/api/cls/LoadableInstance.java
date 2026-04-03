package M6FGR.epic_api.api.cls;

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

class LoadableInstance {
    public static final Logger LOGGER = LogManager.getLogger("ILoadableClass");
    public static final List<Class<? extends ILoadableClass>> LOADED_CLASSES = new ArrayList<>();
    // to specify classes that had an error loading
    public static boolean LOADED;

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
                        Class<?> cls = Class.forName(className);
                        // Ensure it's not the interface itself or an abstract class
                        if (!loadedNames.contains(cls.getName()) && !LOADED) {
                            LOGGER.error("Class: [{}] was never loaded!", className);
                        }
                    } catch (ClassNotFoundException ignored) {
                        // This happens if a class has missing dependencies; safe to skip.
                    }
                });
    }

    public static boolean isClass(Class<?> clazz) {
        return !clazz.isInterface() && !clazz.isEnum() && !clazz.isAnnotation();
    }


}
