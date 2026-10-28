package dev.m6fgr.epic_api.events.mc.client;

import dev.m6fgr.epic_api.events.dispatch.IDispatchableEvent;
import dev.m6fgr.epic_api.events.dispatch.abstracts.DispatchEvent;
import dev.m6fgr.epic_api.events.mc.MinecraftDispatchableEvents;
import net.minecraft.client.Minecraft;

public class ClientTickDispatchEvent extends DispatchEvent implements IDispatchableEvent<ClientTickDispatchEvent> {

    private final Minecraft minecraft;

    public ClientTickDispatchEvent(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    public Minecraft getMinecraft() {
        return this.minecraft;
    }

    @Override
    public ClientTickDispatchEvent post() {
        return MinecraftDispatchableEvents.Client.CLIENT_TICK.postEvent(this);
    }
}
