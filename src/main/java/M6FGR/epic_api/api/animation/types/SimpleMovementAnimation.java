package M6FGR.epic_api.api.animation.types;

import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.MovementAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.camera.EpicFightCameraAPI;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class SimpleMovementAnimation extends MovementAnimation {
    protected float movementSpeed;
    public SimpleMovementAnimation(boolean isRepeat, float movementSpeed, AnimationManager.AnimationAccessor<? extends MovementAnimation> accessor, AssetAccessor<? extends Armature> armature) {
        super(isRepeat, accessor, armature);
        this.movementSpeed = movementSpeed;
    }
    public SimpleMovementAnimation(boolean isRepeat, AnimationManager.AnimationAccessor<? extends MovementAnimation> accessor, AssetAccessor<? extends Armature> armature) {
        super(isRepeat, accessor, armature);
    }

    public SimpleMovementAnimation(float transitionTime, boolean isRepeat, float movementSpeed, AnimationManager.AnimationAccessor<? extends MovementAnimation> accessor, AssetAccessor<? extends Armature> armature) {
        super(transitionTime, isRepeat, accessor, armature);
        this.movementSpeed = movementSpeed;
    }

    public SimpleMovementAnimation(float transitionTime, boolean isRepeat, String path, AssetAccessor<? extends Armature> armature) {
        super(transitionTime, isRepeat, path, armature);
    }

    public float getPlaySpeed(LivingEntityPatch<?> entitypatch, DynamicAnimation animation) {
        if (animation.isLinkAnimation()) {
            return 1.0F;
        } else {
            float i = 1.0F;
            if (Math.abs(entitypatch.getOriginal().walkAnimation.speed() - entitypatch.getOriginal().walkAnimation.speed(1.0F)) < 0.007F) {
                i *= entitypatch.getOriginal().walkAnimation.speed() * this.movementSpeed;
            }

            return i;
        }
    }

    @Override
    public boolean canBePlayedReverse() {
        if (EpicFightCameraAPI.getInstance().isTPSMode()) {
            return EpicFightCameraAPI.getInstance().isLockingOnTarget() || EpicFightCameraAPI.getInstance().isLerpingFpv() || EpicFightCameraAPI.getInstance().isZooming();
        }
        return true;
    }
}
