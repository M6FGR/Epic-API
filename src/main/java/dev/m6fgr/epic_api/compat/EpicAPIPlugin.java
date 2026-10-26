package dev.m6fgr.epic_api.compat;

import dev.m6fgr.epic_api.compat.plugin.CompatibilityPlugin;
import dev.m6fgr.epic_api.main.EpicAPI;
import net.neoforged.bus.api.IEventBus;

public class EpicAPIPlugin implements CompatibilityPlugin {
    @Override
    public String getTargetModID() {
        return EpicAPI.MOD_ID;
    }

    @Override
    public boolean isClientOnly() {
        return true;
    }

    @Override
    public void onInitialize(IEventBus neoBus, IEventBus modBus) {}
}
