package M6FGR.epic_api.events.mc.server;

import M6FGR.epic_api.events.IEventHook;
import M6FGR.epic_api.events.mc.MinecraftEventHooks;
import net.minecraft.server.MinecraftServer;
import yesman.epicfight.api.event.Event;

public class ServerStopEventHook extends Event implements IEventHook<ServerStopEventHook> {
    private final MinecraftServer server;

    public ServerStopEventHook(MinecraftServer server) {
        this.server = server;
    }

    public MinecraftServer getServer() {
        return this.server;
    }

    @Override
    public ServerStopEventHook post() {
        return MinecraftEventHooks.Server.SERVER_STOP.post(this);
    }
}
