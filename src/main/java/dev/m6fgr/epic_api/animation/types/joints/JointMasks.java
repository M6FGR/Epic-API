package dev.m6fgr.epic_api.animation.types.joints;

import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.client.animation.property.JointMask;
import yesman.epicfight.api.client.animation.property.JointMaskEntry;
import yesman.epicfight.gameasset.Armatures;

import java.util.Arrays;
import java.util.function.Supplier;

public enum JointMasks implements dev.m6fgr.epic_api.animation.types.joints.JointMask {
    BIPED_ROOT_UPPER_JOINTS(() -> joints(
            Armatures.BIPED.get().torso,
            Armatures.BIPED.get().chest,
            Armatures.BIPED.get().head,
            // right arms
            Armatures.BIPED.get().shoulderR, Armatures.BIPED.get().armR,
            Armatures.BIPED.get().handR, Armatures.BIPED.get().toolR,
            // left arms
            Armatures.BIPED.get().shoulderL, Armatures.BIPED.get().armL,
            Armatures.BIPED.get().handL, Armatures.BIPED.get().toolL
    )),
    BIPED_LEFT_ARM(() -> joints(
            Armatures.BIPED.get().shoulderL, Armatures.BIPED.get().armL,
            Armatures.BIPED.get().handL, Armatures.BIPED.get().toolL
    )),
    BIPED_RIGHT_ARM(() -> joints(
            Armatures.BIPED.get().shoulderR, Armatures.BIPED.get().armR,
            Armatures.BIPED.get().handR, Armatures.BIPED.get().toolR
    )),
    BIPED_LEGS(() -> joints(
            Armatures.BIPED.get().thighR, Armatures.BIPED.get().kneeR,
            Armatures.BIPED.get().legR, Armatures.BIPED.get().thighL,
            Armatures.BIPED.get().kneeL, Armatures.BIPED.get().legL
    )),
    NONE(JointMasks::joints);

    private final Supplier<Joint[]> jointsSupplier;
    private final int universalID;

    JointMasks(Supplier<Joint[]> jointsSupplier) {
        this.jointsSupplier = jointsSupplier;
        this.universalID = dev.m6fgr.epic_api.animation.types.joints.JointMask.ENUM_MANAGER.assign(this);
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
    @Override
    public JointMaskEntry getAsEntry() {
        return createEntry(this.jointsSupplier.get());
    }

    @Override
    public Joint[] getJoints() {
        return this.jointsSupplier.get();
    }

    @Override
    public int universalOrdinal() {
        return this.universalID;
    }
}