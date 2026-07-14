package M6FGR.epic_api.events.mc.player;

import M6FGR.epic_api.events.IEventHook;
import M6FGR.epic_api.events.mc.MinecraftEventHooks;
import net.minecraft.world.entity.player.Player;
import yesman.epicfight.api.event.Event;

public abstract class PlayerTickEventHook extends Event {
    private final Player player;
    public PlayerTickEventHook(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }

    public static class Pre extends PlayerTickEventHook implements IEventHook<Pre> {
        public Pre(Player player) {
            super(player);
        }

        @Override
        public Pre post() {
            return MinecraftEventHooks.Player.TICK_PRE.post(this);
        }
    }

    public static class Post extends PlayerTickEventHook implements IEventHook<Post>{
        public Post(Player player) {
            super(player);
        }

        @Override
        public Post post() {
            return MinecraftEventHooks.Player.TICK_POST.post(this);
        }
    }
}
