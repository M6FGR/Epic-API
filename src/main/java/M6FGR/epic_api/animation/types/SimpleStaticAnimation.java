package M6FGR.epic_api.animation.types;

import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.animation.Layer;
import yesman.epicfight.api.client.animation.property.ClientAnimationProperties;
import yesman.epicfight.api.client.animation.property.JointMask;
import yesman.epicfight.api.client.animation.property.JointMaskEntry;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.gameasset.Armatures;

import java.util.Arrays;
import java.util.function.Supplier;

public class SimpleStaticAnimation extends StaticAnimation {
    public SimpleStaticAnimation(float transitionTime, boolean repeatable, AnimationManager.AnimationAccessor<? extends SimpleStaticAnimation> animation, AssetAccessor<? extends Armature> armature) {
        super(transitionTime, repeatable, animation, armature);
    }

    public SimpleStaticAnimation(boolean repeatable, AnimationManager.AnimationAccessor<? extends SimpleStaticAnimation> animation, AssetAccessor<? extends Armature> armature) {
        super(repeatable, animation, armature);
    }

    public SimpleStaticAnimation(AnimationManager.AnimationAccessor<? extends SimpleStaticAnimation> animation, AssetAccessor<? extends Armature> armature) {
        super(true, animation, armature);
    }

    public SimpleStaticAnimation withPriority(Layer.Priority priority) {
        this.addProperty(ClientAnimationProperties.PRIORITY, priority);
        return this;
    }

    public SimpleStaticAnimation withLayer(Layer.LayerType layer) {
        this.addProperty(ClientAnimationProperties.LAYER_TYPE, layer);
        return this;
    }

    public SimpleStaticAnimation withJointMask(JointMasks masks) {
        this.addProperty(ClientAnimationProperties.JOINT_MASK, masks.getEntry());
        return this;
    }
    public SimpleStaticAnimation withJointMask(JointMaskEntry mask) {
        this.addProperty(ClientAnimationProperties.JOINT_MASK, mask);
        return this;
    }

    public enum JointMasks {
        ROOT_UPPER_JOINTS(() -> joints(
                Armatures.BIPED.get().rootJoint, Armatures.BIPED.get().torso,
                Armatures.BIPED.get().chest, Armatures.BIPED.get().head,
                Armatures.BIPED.get().shoulderR, Armatures.BIPED.get().armR,
                Armatures.BIPED.get().handR, Armatures.BIPED.get().toolR,
                Armatures.BIPED.get().shoulderL, Armatures.BIPED.get().armL,
                Armatures.BIPED.get().handL, Armatures.BIPED.get().toolL
        )),
        LEFT_ARM(() -> joints(
                Armatures.BIPED.get().shoulderL, Armatures.BIPED.get().armL,
                Armatures.BIPED.get().handL, Armatures.BIPED.get().toolL
        )),
        RIGHT_ARM(() -> joints(
                Armatures.BIPED.get().shoulderR, Armatures.BIPED.get().armR,
                Armatures.BIPED.get().handR, Armatures.BIPED.get().toolR
        )),
        LEGS(() -> joints(
                Armatures.BIPED.get().thighR, Armatures.BIPED.get().kneeR,
                Armatures.BIPED.get().legR, Armatures.BIPED.get().thighL,
                Armatures.BIPED.get().kneeL, Armatures.BIPED.get().legL
        )),
        NONE(JointMasks::joints);

        private final Supplier<Joint[]> jointsSupplier;

        JointMasks(Supplier<Joint[]> jointsSupplier) {
            this.jointsSupplier = jointsSupplier;
        }

        /*
          Internal helper to clean up enum definitions.
        */
        private static Joint[] joints(Joint... joints) {
            return joints;
        }

        /*
         * Static factory to create a custom JointMaskEntry.
         * If no joints are passed, it effectively acts as an "EMPTY" mask.
         */
        public static JointMaskEntry createEntry(Joint... joints) {
            JointMask[] masks = Arrays.stream(joints)
                    .map(j -> JointMask.of(j.getName()))
                    .toArray(JointMask[]::new);

            return JointMaskEntry.builder()
                    .defaultMask(JointMask.JointMaskSet.of(masks))
                    .create();
        }

        /*
         Returns the pre-defined mask entry for this enum constant.
         */
        public JointMaskEntry getEntry() {
            return createEntry(this.jointsSupplier.get());
        }
    }
}