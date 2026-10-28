package dev.m6fgr.epic_api.events.mc.server;

import dev.m6fgr.epic_api.events.dispatch.IDispatchableEvent;
import dev.m6fgr.epic_api.events.dispatch.abstracts.DispatchEvent;
import dev.m6fgr.epic_api.events.mc.MinecraftDispatchableEvents;
import net.minecraft.server.MinecraftServer;

public class ServerStopDispatchEvent extends DispatchEvent implements IDispatchableEvent<ServerStopDispatchEvent> {
    private final MinecraftServer server;

    public ServerStopDispatchEvent(MinecraftServer server) {
        this.server = server;
    }

    public MinecraftServer getServer() {
        return this.server;
    }

    @Override
    public ServerStopDispatchEvent post() {
        return MinecraftDispatchableEvents.Server.SERVER_STOP.postEvent(this);
    }
}
