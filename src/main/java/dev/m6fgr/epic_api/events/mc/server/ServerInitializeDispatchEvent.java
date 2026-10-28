package dev.m6fgr.epic_api.events.mc.server;

import dev.m6fgr.epic_api.events.dispatch.IDispatchableEvent;
import dev.m6fgr.epic_api.events.dispatch.abstracts.DispatchEvent;
import dev.m6fgr.epic_api.events.mc.MinecraftDispatchableEvents;
import net.minecraft.server.MinecraftServer;

public abstract class ServerInitializeDispatchEvent extends DispatchEvent implements IDispatchableEvent<ServerInitializeDispatchEvent> {

    protected final MinecraftServer server;

    public ServerInitializeDispatchEvent(MinecraftServer server) {
        this.server = server;
    }

    public MinecraftServer getServer() {
        return this.server;
    }


    public abstract ServerInitializeDispatchEvent post();

    public static class Pre extends ServerInitializeDispatchEvent {
        public Pre(MinecraftServer server) {
            super(server);
        }

        @Override
        public Pre post() {
            return MinecraftDispatchableEvents.Server.SERVER_START_PRE.postEvent(this);
        }
    }

    public static class Post extends ServerInitializeDispatchEvent {
        public Post(MinecraftServer server) {
            super(server);
        }

        @Override
        public Post post() {
            return MinecraftDispatchableEvents.Server.SERVER_START_POST.postEvent(this);
        }
    }

}
