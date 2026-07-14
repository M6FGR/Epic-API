package M6FGR.epic_api.events.player;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.entity.eventlistener.DetachablePlayerEvent;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class AddPlayerMotionsEvent extends Event implements IModBusEvent, DetachablePlayerEvent<PlayerPatch<?>> {

    public static final EventType<AddPlayerMotionsEvent> TYPE = new EventType<>(null);

    private final Animator animator;
    private final PlayerPatch<?> playerPatch;


    public AddPlayerMotionsEvent(Animator animator, PlayerPatch<?> playerPatch) {
        this.animator = animator;
        this.playerPatch = playerPatch;
    }

    public PlayerPatch<?> getPlayerPatch() {
        return this.playerPatch;
    }

    @Override
    public void setCanceled(boolean val) {
        super.setCanceled(val);
    }

    @Override
    public boolean isCanceled() {
        return super.isCanceled();
    }

    public Animator getAnimator() {
        return this.animator;
    }
}
