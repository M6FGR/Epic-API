package M6FGR.epic_api.events.mc.server;

import M6FGR.epic_api.events.IEventHook;
import M6FGR.epic_api.events.mc.MinecraftEventHooks;
import net.minecraft.server.MinecraftServer;
import yesman.epicfight.api.event.Event;

import java.util.function.BooleanSupplier;

public class ServerTickEventHook extends Event implements IEventHook<ServerTickEventHook> {
    private final MinecraftServer server;
    private final BooleanSupplier boolSupplier;

    public ServerTickEventHook(MinecraftServer server, BooleanSupplier supplier) {
        this.server = server;
        this.boolSupplier = supplier;
    }

    public MinecraftServer getServer() {
        return this.server;
    }

    public BooleanSupplier getBoolSupplier() {
        return this.boolSupplier;
    }

    @Override
    public ServerTickEventHook post() {
        return MinecraftEventHooks.Server.SERVER_TICK.post(this);
    }
}
