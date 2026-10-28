package dev.m6fgr.epic_api.events.mc.client;

import dev.m6fgr.epic_api.events.dispatch.IDispatchableEvent;
import dev.m6fgr.epic_api.events.dispatch.abstracts.DispatchEvent;
import dev.m6fgr.epic_api.events.dispatch.marks.HybridEvent;
import dev.m6fgr.epic_api.events.mc.MinecraftDispatchableEvents;
import net.minecraft.client.Minecraft;

@HybridEvent
public abstract class ClientInitializeDispatchEvent extends DispatchEvent implements IDispatchableEvent<ClientInitializeDispatchEvent> {
    @Override
    public abstract ClientInitializeDispatchEvent post();

    protected final Minecraft minecraft;

    public ClientInitializeDispatchEvent(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    public Minecraft getMinecraft() {
        return this.minecraft;
    }

    public static class Pre extends ClientInitializeDispatchEvent {

        public Pre(Minecraft minecraft) {
            super(minecraft);
        }

        @Override
        public ClientInitializeDispatchEvent post() {
            return MinecraftDispatchableEvents.Client.CLIENT_START_PRE.postEvent(this);
        }
    }

    public static class Post extends ClientInitializeDispatchEvent {

        public Post(Minecraft minecraft) {
            super(minecraft);
        }

        @Override
        public ClientInitializeDispatchEvent post() {
            return MinecraftDispatchableEvents.Client.CLIENT_START_POST.postEvent(this);
        }
    }
}
