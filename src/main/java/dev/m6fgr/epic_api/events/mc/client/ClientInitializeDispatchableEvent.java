package dev.m6fgr.epic_api.events.mc.client;

import dev.m6fgr.epic_api.events.dispatch.IDispatchableEvent;
import dev.m6fgr.epic_api.events.dispatch.abstracts.DispatchableEvent;
import dev.m6fgr.epic_api.events.dispatch.marks.HybridEvent;
import dev.m6fgr.epic_api.events.mc.MinecraftDispatchableEvents;
import net.minecraft.client.Minecraft;

@HybridEvent
public abstract class ClientInitializeDispatchableEvent extends DispatchableEvent implements IDispatchableEvent<ClientInitializeDispatchableEvent> {
    @Override
    public abstract ClientInitializeDispatchableEvent post();

    protected final Minecraft minecraft;

    public ClientInitializeDispatchableEvent(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    public Minecraft getMinecraft() {
        return this.minecraft;
    }

    public static class Pre extends ClientInitializeDispatchableEvent {

        public Pre(Minecraft minecraft) {
            super(minecraft);
        }

        @Override
        public ClientInitializeDispatchableEvent post() {
            return MinecraftDispatchableEvents.Client.CLIENT_START_PRE.postEvent(this);
        }
    }

    public static class Post extends ClientInitializeDispatchableEvent {

        public Post(Minecraft minecraft) {
            super(minecraft);
        }

        @Override
        public ClientInitializeDispatchableEvent post() {
            return MinecraftDispatchableEvents.Client.CLIENT_START_POST.postEvent(this);
        }
    }
}
