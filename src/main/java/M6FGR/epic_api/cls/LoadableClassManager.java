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
    public static boolean LOADED;



    // in case you forgot to load a class, this will help
    public static void checkUnloaded(String modId) {
        // it gets the package from the mod-id, as so:
        IModFileInfo modFile = ModList.get().getModFileById(modId);
        if (modFile == null)
            return;

        // then it gets the scan result from the file, and the class type of ILoadableClass
        ModFileScanData scanData = modFile.getFile().getScanResult();
        Type loadableClsType = Type.getType(ILoadableClass.class);

        // we stream the list from a Class<?> to a String (names), so it's easier to search
        Set<String> loadedNames = LOADED_CLASSES.stream()
                .map(Class::getName)
                .collect(Collectors.toSet());

        // using the scan data, we scan the package based on the mod-id here
        scanData.getClasses().stream()
                // ONLY give is classes that implements ILoadableClass
                .filter(data -> data.interfaces().contains(loadableClsType))
                .forEach(data -> {
                    // if found, we get its name
                    String className = data.clazz().getClassName();

                    if (loadedNames.contains(className)) {
                        return; // skip, we already loaded this one
                    }
                    // in case the guard-wall above didn't work, we print out what's forgotten
                    LOGGER.warn("Class [{}] is never loaded!", className);
                });
    }

    public static boolean isClass(Class<?> clazz) {
        return !clazz.isInterface() && !clazz.isEnum() && !clazz.isAnnotation();
    }
}
