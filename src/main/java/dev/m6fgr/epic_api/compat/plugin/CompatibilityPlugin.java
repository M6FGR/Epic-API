package dev.m6fgr.epic_api.compat.plugin;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;

public interface CompatibilityPlugin {
    String getTargetModID();

    boolean isClientOnly();

    void onInitialize(IEventBus neoBus);

    default boolean shouldInit() {
        // if empty, it will not initialize, not a compatibility!
        if (this.getTargetModID() == null || this.getTargetModID().isBlank() || this.getTargetModID().isEmpty()) {
            return false;
        }
        return ModList.get().isLoaded(this.getTargetModID());
    }
}
