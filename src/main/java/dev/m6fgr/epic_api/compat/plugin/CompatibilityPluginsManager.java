package dev.m6fgr.epic_api.compat.plugin;

import dev.m6fgr.epic_api.cls.maps.BasicMap;
import dev.m6fgr.epic_api.main.EpicAPI;
import dev.m6fgr.epic_api.utils.EnvironmentHelper;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;

public final class CompatibilityPluginsManager {
    private static final Logger LOGGER = LogManager.getLogger(CompatibilityPluginsManager.class.getSimpleName());
    private static final BasicMap<String, CompatibilityPlugin> PLUGIN_BY_MOD_ID = new BasicMap<>();

    @SafeVarargs
    public static void registerPlugins(Class<? extends CompatibilityPlugin>... pluginClasses) {
        for (Class<? extends CompatibilityPlugin> pluginClass : pluginClasses) {
            String simpleName = pluginClass.getSimpleName();
            try {
                Constructor<? extends CompatibilityPlugin> constructor = pluginClass.getDeclaredConstructor();
                constructor.setAccessible(true);
                CompatibilityPlugin plugin = constructor.newInstance();

                if (plugin.shouldInit()) {
                    if (plugin.isClientOnly()) {
                        if (!EnvironmentHelper.getEnvironment().isClient()) {
                            continue;
                        }
                    }
                    plugin.onInitialize(NeoForge.EVENT_BUS);
                }

                PLUGIN_BY_MOD_ID.put(plugin.getTargetModID(), plugin);
                LOGGER.info("Initialized Compatibility Plugin: [{}]", simpleName);
            } catch (NoSuchMethodException e) {
                EpicAPI.err("Failed to initialize plugin [{}]: Missing default no-arg constructor!", simpleName);
            } catch (Exception e) {
                EpicAPI.err("Failed to initialize plugin [{}], {}", pluginClass.getName(), e);
                e.printStackTrace();
            }
        }
    }

    public boolean isPluginLoadedFor(String modID) {
        return PLUGIN_BY_MOD_ID.get(modID) != null;
    }
}