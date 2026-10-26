package dev.m6fgr.epic_api.events.mc.server;

import dev.m6fgr.epic_api.events.dispatch.IDispatchableEvent;
import dev.m6fgr.epic_api.events.dispatch.abstracts.DispatchableEvent;
import dev.m6fgr.epic_api.events.mc.MinecraftDispatchableEvents;
import net.minecraft.server.MinecraftServer;

import java.util.function.BooleanSupplier;

public class ServerTickDispatchableEvent extends DispatchableEvent implements IDispatchableEvent<ServerTickDispatchableEvent> {
    private final MinecraftServer server;
    private final BooleanSupplier boolSupplier;

    public ServerTickDispatchableEvent(MinecraftServer server, BooleanSupplier hasTimeLeft) {
        this.server = server;
        this.boolSupplier = hasTimeLeft;
    }

    public MinecraftServer getServer() {
        return this.server;
    }

    public boolean hasTimeLeft() {
        return this.boolSupplier.getAsBoolean();
    }

    @Override
    public ServerTickDispatchableEvent post() {
        return MinecraftDispatchableEvents.Server.SERVER_TICK.postEvent(this);
    }
}
