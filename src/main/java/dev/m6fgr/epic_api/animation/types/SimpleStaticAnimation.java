package dev.m6fgr.epic_api.animation.types;

import dev.m6fgr.epic_api.animation.SimpleAnimationProperty;
import dev.m6fgr.epic_api.animation.types.joints.JointMask;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.animation.Layer;
import yesman.epicfight.api.client.animation.property.ClientAnimationProperties;
import yesman.epicfight.api.client.animation.property.JointMaskEntry;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class SimpleStaticAnimation extends StaticAnimation {
    public SimpleStaticAnimation(float transitionTime, boolean repeatable, AnimationManager.AnimationAccessor<? extends SimpleStaticAnimation> animation, AssetAccessor<? extends Armature> armature) {
        super(transitionTime, repeatable, animation, armature);
    }

    public SimpleStaticAnimation(boolean repeatable, AnimationManager.AnimationAccessor<? extends SimpleStaticAnimation> animation, AssetAccessor<? extends Armature> armature) {
        this(0.16F, repeatable, animation, armature);
    }

    public SimpleStaticAnimation(AnimationManager.AnimationAccessor<? extends SimpleStaticAnimation> animation, AssetAccessor<? extends Armature> armature) {
        this(true, animation, armature);
    }


    public SimpleStaticAnimation withPriority(Layer.Priority priority) {
        this.addProperty(ClientAnimationProperties.PRIORITY, priority);
        return this;
    }

    public SimpleStaticAnimation withLayer(Layer.LayerType layer) {
        this.addProperty(ClientAnimationProperties.LAYER_TYPE, layer);
        return this;
    }

    public SimpleStaticAnimation withJointMask(JointMask masks) {
        this.addProperty(ClientAnimationProperties.JOINT_MASK, masks.getAsEntry());
        return this;
    }
    public SimpleStaticAnimation withJointMask(JointMaskEntry mask) {
        this.addProperty(ClientAnimationProperties.JOINT_MASK, mask);
        return this;
    }

    @Override
    public float getPlaySpeed(LivingEntityPatch<?> entitypatch, DynamicAnimation animation) {
        if (this.properties.containsKey(SimpleAnimationProperty.PLAY_SPEED)) {
            return this.getProperty(SimpleAnimationProperty.PLAY_SPEED).orElse(1.0F);
        }
        return super.getPlaySpeed(entitypatch, animation);
    }
}