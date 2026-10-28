package dev.m6fgr.epic_api.events.mc.player;

import dev.m6fgr.epic_api.events.dispatch.IDispatchableEvent;
import dev.m6fgr.epic_api.events.dispatch.abstracts.DispatchEvent;
import dev.m6fgr.epic_api.events.mc.MinecraftDispatchableEvents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public abstract class PlayerJoinDispatchEvent extends DispatchEvent implements IDispatchableEvent<PlayerJoinDispatchEvent> {

    private final Player player;
    private final Level level;
    private final boolean fromDisk;

    public PlayerJoinDispatchEvent(Player player, Level level, boolean isFromDisk) {
        this.player = player;
        this.level = level;
        this.fromDisk = isFromDisk;
    }

    public PlayerJoinDispatchEvent(Player player, Level level) {
        this(player, level, false);
    }

    public boolean isFromDisk() {
        return this.fromDisk;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Level getLevel() {
        return this.level;
    }

    public static class Client extends PlayerJoinDispatchEvent {

        public Client(Player player, ClientLevel level) {
            super(player, level);
        }

        @Override
        public Client post() {
            return MinecraftDispatchableEvents.Player.JOIN_CLIENT.postEvent(this);
        }
    }

    public static class Server extends PlayerJoinDispatchEvent {
        public Server(Player player, ServerLevel level) {
            super(player, level);
        }

        @Override
        public Server post() {
            return MinecraftDispatchableEvents.Player.JOIN_SERVER.postEvent(this);
        }
    }
}
