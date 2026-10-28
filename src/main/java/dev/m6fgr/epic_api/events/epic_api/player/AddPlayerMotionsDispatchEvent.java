package dev.m6fgr.epic_api.events.epic_api.player;

import dev.m6fgr.epic_api.events.dispatch.IDispatchableEvent;
import dev.m6fgr.epic_api.events.dispatch.abstracts.DispatchEvent;
import dev.m6fgr.epic_api.events.dispatch.marks.HybridEvent;
import dev.m6fgr.epic_api.events.epic_api.EpicAPIDispatchableEvents;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

@HybridEvent
public class AddPlayerMotionsDispatchEvent extends DispatchEvent implements IDispatchableEvent<AddPlayerMotionsDispatchEvent> {

    private final Animator animator;
    private final PlayerPatch<?> playerPatch;

    public AddPlayerMotionsDispatchEvent(Animator animator, PlayerPatch<?> playerPatch) {
        this.animator = animator;
        this.playerPatch = playerPatch;
    }

    public void addAnimation(LivingMotion motion, AnimationAccessor<? extends StaticAnimation> animation) {
        this.animator.addLivingAnimation(motion, animation);
    }

    public Animator getAnimator() {
        return this.animator;
    }

    public PlayerPatch<?> getPlayerPatch() {
        return this.playerPatch;
    }

    @Override
    public AddPlayerMotionsDispatchEvent post() {
        return EpicAPIDispatchableEvents.Player.ADD_MOTIONS.postEvent(this);
    }
}
