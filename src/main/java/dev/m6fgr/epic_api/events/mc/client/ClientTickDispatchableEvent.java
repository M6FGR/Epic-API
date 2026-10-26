package dev.m6fgr.epic_api.events.mc.client;

import dev.m6fgr.epic_api.events.dispatch.IDispatchableEvent;
import dev.m6fgr.epic_api.events.dispatch.abstracts.DispatchableEvent;
import dev.m6fgr.epic_api.events.mc.MinecraftDispatchableEvents;
import net.minecraft.client.Minecraft;

public class ClientTickDispatchableEvent extends DispatchableEvent implements IDispatchableEvent<ClientTickDispatchableEvent> {

    private final Minecraft minecraft;

    public ClientTickDispatchableEvent(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    public Minecraft getMinecraft() {
        return this.minecraft;
    }

    @Override
    public ClientTickDispatchableEvent post() {
        return MinecraftDispatchableEvents.Client.CLIENT_TICK.postEvent(this);
    }
}
