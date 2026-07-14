package M6FGR.epic_api.events.mc.server;

import M6FGR.epic_api.events.IEventHook;
import M6FGR.epic_api.events.mc.MinecraftEventHooks;
import net.minecraft.server.MinecraftServer;
import yesman.epicfight.api.event.Event;

public abstract class ServerStartEventHook extends Event {

    private final MinecraftServer server;

    public ServerStartEventHook(MinecraftServer server) {
        this.server = server;
    }

    public MinecraftServer getServer() {
        return this.server;
    }

    public static class Pre extends ServerStartEventHook implements IEventHook<Pre> {
        public Pre(MinecraftServer server) {
            super(server);
        }

        @Override
        public Pre post() {
            return MinecraftEventHooks.Server.SERVER_START_PRE.post(this);
        }
    }

    public static class Post extends ServerStartEventHook implements IEventHook<Post> {
        public Post(MinecraftServer server) {
            super(server);
        }

        @Override
        public Post post() {
            return MinecraftEventHooks.Server.SERVER_START_POST.post(this);
        }
    }

}
