package dev.m6fgr.epic_api.main;

import dev.m6fgr.epic_api.events.bus.ModBus;
import dev.m6fgr.epic_api.events.bus.NeoBus;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public sealed interface EpicAPI permits EpicAPIMod {
    String MOD_ID = "epic_api";
    String MOD_NAME = "EpicAPI";
    Logger LOGGER = LogManager.getLogger(MOD_NAME);

    static EpicAPI getInstance() {
        return EpicAPIMod.instance;
    }

    ModContainer getModContainer();

    ModBus getModBus();

    boolean isDeveloperEnvironment();

    ResourceLocation identifier(String path);

    NeoBus getNeoBus();

}
