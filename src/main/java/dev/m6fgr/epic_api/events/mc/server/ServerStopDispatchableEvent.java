package dev.m6fgr.epic_api.events.mc.server;

import dev.m6fgr.epic_api.events.dispatch.IDispatchableEvent;
import dev.m6fgr.epic_api.events.dispatch.abstracts.DispatchableEvent;
import dev.m6fgr.epic_api.events.mc.MinecraftDispatchableEvents;
import net.minecraft.server.MinecraftServer;

public class ServerStopDispatchableEvent extends DispatchableEvent implements IDispatchableEvent<ServerStopDispatchableEvent> {
    private final MinecraftServer server;

    public ServerStopDispatchableEvent(MinecraftServer server) {
        this.server = server;
    }

    public MinecraftServer getServer() {
        return this.server;
    }

    @Override
    public ServerStopDispatchableEvent post() {
        return MinecraftDispatchableEvents.Server.SERVER_STOP.postEvent(this);
    }
}
