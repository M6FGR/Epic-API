package M6FGR.epic_api.events.mc.player;

import M6FGR.epic_api.events.IEventHook;
import M6FGR.epic_api.events.mc.MinecraftEventHooks;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import yesman.epicfight.api.event.Event;

public abstract class PlayerJoinEventHook extends Event {

    private final Player player;
    private final Level level;
    private final boolean fromDisk;

    public PlayerJoinEventHook(Player player, Level level, boolean isFromDisk) {
        this.player = player;
        this.level = level;
        this.fromDisk = isFromDisk;
    }

    public PlayerJoinEventHook(Player player, Level level) {
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

    public static class Client extends PlayerJoinEventHook implements IEventHook<Client> {

        public Client(Player player, ClientLevel level) {
            super(player, level);
        }

        @Override
        public Client post() {
            return MinecraftEventHooks.Player.JOIN_CLIENT.post(this);
        }
    }

    public static class Server extends PlayerJoinEventHook implements IEventHook<Server> {
        public Server(Player player, ServerLevel level) {
            super(player, level);
        }

        @Override
        public Server post() {
            return MinecraftEventHooks.Player.JOIN_SERVER.post(this);
        }
    }
}
