package M6FGR.epic_api.animation.types;

import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.animation.Layer;
import yesman.epicfight.api.client.animation.property.JointMaskEntry;
import yesman.epicfight.api.client.camera.EpicFightCameraAPI;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class SimpleMovementAnimation extends SimpleStaticAnimation {
    protected float movementSpeed;

    public SimpleMovementAnimation(boolean isRepeat, float movementSpeed, AnimationManager.AnimationAccessor<? extends SimpleStaticAnimation> accessor, AssetAccessor<? extends Armature> armature) {
        super(isRepeat, accessor, armature);
        this.movementSpeed = movementSpeed;
    }

    public SimpleMovementAnimation(boolean isRepeat, AnimationManager.AnimationAccessor<? extends SimpleStaticAnimation> accessor, AssetAccessor<? extends Armature> armature) {
        super(isRepeat, accessor, armature);
    }

    public SimpleMovementAnimation(float transitionTime, boolean isRepeat, float movementSpeed, AnimationManager.AnimationAccessor<? extends SimpleStaticAnimation> accessor, AssetAccessor<? extends Armature> armature) {
        super(transitionTime, isRepeat, accessor, armature);
        this.movementSpeed = movementSpeed;
    }

    @Override
    public SimpleMovementAnimation withPriority(Layer.Priority priority) {
        super.withPriority(priority);
        return this;
    }
    @Override
    public SimpleMovementAnimation withLayer(Layer.LayerType layer) {
        super.withLayer(layer);
        return this;
    }
    @Override
    public SimpleMovementAnimation withJointMask(JointMasks masks) {
        super.withJointMask(masks);
        return this;
    }
    @Override
    public SimpleMovementAnimation withJointMask(JointMaskEntry mask) {
        super.withJointMask(mask);
        return this;
    }

    @Override
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
