package dev.m6fgr.epic_api.events.epic_api.dispatch;

import dev.m6fgr.epic_api.events.dispatch.abstracts.DispatchableEvent;
import net.neoforged.fml.ModLoader;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.ApiStatus.Internal;

// Used on @SubscribeEvent to dispatch it anywhere, no specific methods needed
public class DispatchEvent extends DispatchableEvent {
    @Internal
    public DispatchEvent() {}

    @Override
    public boolean canBeOverridden() {
        return false;
    }

    public static DispatchEvent postEvent() {
        DispatchEvent instance = new DispatchEvent();
        NeoForge.EVENT_BUS.post(instance);
        ModLoader.postEvent(instance);
        return instance;
    }

}
