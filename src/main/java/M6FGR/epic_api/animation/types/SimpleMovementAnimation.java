package M6FGR.epic_api.animation.types;

import M6FGR.epic_api.animation.SimpleAnimationProperty;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.animation.Layer.LayerType;
import yesman.epicfight.api.client.animation.Layer.Priority;
import yesman.epicfight.api.client.animation.property.JointMaskEntry;
import yesman.epicfight.api.client.camera.EpicFightCameraAPI;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class SimpleMovementAnimation extends SimpleStaticAnimation {
    protected float movementSpeed;

    public SimpleMovementAnimation(boolean repeatable, float movementSpeed, AnimationAccessor<? extends SimpleStaticAnimation> accessor, AssetAccessor<? extends Armature> armature) {
        super(repeatable, accessor, armature);
        this.movementSpeed = movementSpeed;
    }

    public SimpleMovementAnimation(boolean repeatable, AnimationAccessor<? extends SimpleStaticAnimation> accessor, AssetAccessor<? extends Armature> armature) {
        super(repeatable, accessor, armature);
    }

    public SimpleMovementAnimation(float transitionTime, boolean repeatable, float movementSpeed, AnimationAccessor<? extends SimpleStaticAnimation> accessor, AssetAccessor<? extends Armature> armature) {
        super(transitionTime, repeatable, accessor, armature);
        this.movementSpeed = movementSpeed;
    }

    @Override
    public SimpleMovementAnimation withPriority(Priority priority) {
        super.withPriority(priority);
        return this;
    }
    @Override
    public SimpleMovementAnimation withLayer(LayerType layer) {
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
        if (this.properties.containsKey(SimpleAnimationProperty.PLAY_SPEED)) {
            this.getProperty(SimpleAnimationProperty.PLAY_SPEED);
        } else {
            return this.addProperty(SimpleAnimationProperty.PLAY_SPEED, this.movementSpeed).getProperty(SimpleAnimationProperty.PLAY_SPEED).orElse(1.0F);
        }
        return super.getPlaySpeed(entitypatch, animation);
    }

    @Override
    public boolean canBePlayedReverse() {
        if (EpicFightCameraAPI.getInstance().isTPSMode()) {
            return EpicFightCameraAPI.getInstance().isLockingOnTarget() || EpicFightCameraAPI.getInstance().isLerpingFpv() || EpicFightCameraAPI.getInstance().isZooming();
        }
        return true;
    }
}
