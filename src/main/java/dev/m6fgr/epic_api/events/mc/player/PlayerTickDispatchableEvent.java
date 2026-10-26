package dev.m6fgr.epic_api.events.mc.player;

import dev.m6fgr.epic_api.events.dispatch.IDispatchableEvent;
import dev.m6fgr.epic_api.events.dispatch.abstracts.DispatchableEvent;
import dev.m6fgr.epic_api.events.mc.MinecraftDispatchableEvents;
import net.minecraft.world.entity.player.Player;

public abstract class PlayerTickDispatchableEvent extends DispatchableEvent implements IDispatchableEvent<PlayerTickDispatchableEvent> {
    private final Player player;
    public PlayerTickDispatchableEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }

    @Override
    public abstract PlayerTickDispatchableEvent post();

    public static class Pre extends PlayerTickDispatchableEvent {
        public Pre(Player player) {
            super(player);
        }

        @Override
        public Pre post() {
            return MinecraftDispatchableEvents.Player.TICK_PRE.postEvent(this);
        }
    }

    public static class Post extends PlayerTickDispatchableEvent {
        public Post(Player player) {
            super(player);
        }

        @Override
        public Post post() {
            return MinecraftDispatchableEvents.Player.TICK_POST.postEvent(this);
        }
    }
}
