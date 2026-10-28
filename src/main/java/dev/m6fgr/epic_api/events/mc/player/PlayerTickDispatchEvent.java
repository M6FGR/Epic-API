package dev.m6fgr.epic_api.events.mc.player;

import dev.m6fgr.epic_api.events.dispatch.IDispatchableEvent;
import dev.m6fgr.epic_api.events.dispatch.abstracts.DispatchEvent;
import dev.m6fgr.epic_api.events.mc.MinecraftDispatchableEvents;
import net.minecraft.world.entity.player.Player;

public abstract class PlayerTickDispatchEvent extends DispatchEvent implements IDispatchableEvent<PlayerTickDispatchEvent> {
    private final Player player;
    public PlayerTickDispatchEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }

    @Override
    public abstract PlayerTickDispatchEvent post();

    public static class Pre extends PlayerTickDispatchEvent {
        public Pre(Player player) {
            super(player);
        }

        @Override
        public Pre post() {
            return MinecraftDispatchableEvents.Player.TICK_PRE.postEvent(this);
        }
    }

    public static class Post extends PlayerTickDispatchEvent {
        public Post(Player player) {
            super(player);
        }

        @Override
        public Post post() {
            return MinecraftDispatchableEvents.Player.TICK_POST.postEvent(this);
        }
    }
}
