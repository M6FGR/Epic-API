package M6FGR.epic_api.models.armature;

import M6FGR.epic_api.exception.DeveloperException;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.model.Armature;

import java.util.Map;
// same as HumanoidArmature but with no tools, just a placeholder
@Deprecated(forRemoval = true)
public class NonHumanoidArmature extends Armature {

    public final Joint
            thighR,
            legR,
            kneeR,
            thighL,
            legL,
            kneeL,
            torso,
            chest,
            head,
            shoulderR,
            armR,
            handR,
            elbowR,
            shoulderL,
            armL,
            handL,
            elbowL;

    public NonHumanoidArmature(String name, int jointNumber, Joint rootJoint, Map<String, Joint> jointMap) {
        super(name, jointNumber, rootJoint, jointMap);
        this.thighR = this.getOrThrowException(jointMap, "Thigh_R");
        this.legR = this.getOrThrowException(jointMap, "Leg_R");
        this.kneeR = this.getOrThrowException(jointMap, "Knee_R");
        this.thighL = this.getOrThrowException(jointMap, "Thigh_L");
        this.legL = this.getOrThrowException(jointMap, "Leg_L");
        this.kneeL = this.getOrThrowException(jointMap, "Knee_L");
        this.torso = this.getOrThrowException(jointMap, "Torso");
        this.chest = this.getOrThrowException(jointMap, "Chest");
        this.head = this.getOrThrowException(jointMap, "Head");
        this.shoulderR = this.getOrThrowException(jointMap, "Shoulder_R");
        this.armR = this.getOrThrowException(jointMap, "Arm_R");
        this.handR = this.getOrThrowException(jointMap, "Hand_R");
        this.elbowR = this.getOrThrowException(jointMap, "Elbow_R");
        this.shoulderL = this.getOrThrowException(jointMap, "Shoulder_L");
        this.armL = this.getOrThrowException(jointMap, "Arm_L");
        this.handL = this.getOrThrowException(jointMap, "Hand_L");
        this.elbowL = this.getOrThrowException(jointMap, "Elbow_L");
    }

    public Joint getOrThrowException(Map<String, Joint> jointMap, String name) {
        if (!jointMap.containsKey(name)) {
            DeveloperException.throwEx("Cannot find joint: " + name + "in the armature: " + this.getClass().getSimpleName());
            return Joint.EMPTY;
        }
        return jointMap.get(name);
    }
}
