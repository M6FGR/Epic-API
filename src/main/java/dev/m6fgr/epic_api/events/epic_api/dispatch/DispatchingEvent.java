package dev.m6fgr.epic_api.events.epic_api.dispatch;

import dev.m6fgr.epic_api.events.dispatch.abstracts.DispatchEvent;
import net.neoforged.fml.ModLoader;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.ApiStatus.Internal;

// Used on @SubscribeEvent to dispatch it anywhere, no specific methods needed
public class DispatchingEvent extends DispatchEvent {
    @Internal
    public DispatchingEvent() {}

    @Override
    public boolean canBeOverridden() {
        return false;
    }

    public static DispatchingEvent postEvent() {
        DispatchingEvent instance = new DispatchingEvent();
        NeoForge.EVENT_BUS.post(instance);
        ModLoader.postEvent(instance);
        return instance;
    }

}
