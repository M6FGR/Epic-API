package M6FGR.epic_api.events.epic_api.player;

import M6FGR.epic_api.events.IEventHook;
import M6FGR.epic_api.events.epic_api.EpicAPIEventHooks;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.event.Event;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class AddPlayerMotionsEventHook extends Event implements IEventHook<AddPlayerMotionsEventHook> {

    private final Animator animator;
    private final PlayerPatch<?> playerPatch;

    public AddPlayerMotionsEventHook(Animator animator, PlayerPatch<?> playerPatch) {
        this.animator = animator;
        this.playerPatch = playerPatch;
    }

    public Animator getAnimator() {
        return this.animator;
    }

    public PlayerPatch<?> getPlayerPatch() {
        return this.playerPatch;
    }

    @Override
    public AddPlayerMotionsEventHook post() {
        return EpicAPIEventHooks.Player.ADD_MOTIONS.post(this);
    }
}
