package dev.m6fgr.epic_api.events.mc.server;

import dev.m6fgr.epic_api.events.dispatch.IDispatchableEvent;
import dev.m6fgr.epic_api.events.dispatch.abstracts.DispatchableEvent;
import dev.m6fgr.epic_api.events.mc.MinecraftDispatchableEvents;
import net.minecraft.server.MinecraftServer;

public abstract class ServerInitializeDispatchableEvent extends DispatchableEvent implements IDispatchableEvent<ServerInitializeDispatchableEvent> {

    protected final MinecraftServer server;

    public ServerInitializeDispatchableEvent(MinecraftServer server) {
        this.server = server;
    }

    public MinecraftServer getServer() {
        return this.server;
    }


    public abstract ServerInitializeDispatchableEvent post();

    public static class Pre extends ServerInitializeDispatchableEvent {
        public Pre(MinecraftServer server) {
            super(server);
        }

        @Override
        public Pre post() {
            return MinecraftDispatchableEvents.Server.SERVER_START_PRE.postEvent(this);
        }
    }

    public static class Post extends ServerInitializeDispatchableEvent {
        public Post(MinecraftServer server) {
            super(server);
        }

        @Override
        public Post post() {
            return MinecraftDispatchableEvents.Server.SERVER_START_POST.postEvent(this);
        }
    }

}
