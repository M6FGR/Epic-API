package M6FGR.epic_api.models.armature;

import M6FGR.epic_api.main.EpicAPI;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.exception.AssetLoadingException;
import yesman.epicfight.api.model.Armature;

import java.util.Map;
// same as HumanoidArmature but with no tools, just a placeholder
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
        this.thighR = this.getOrDevException(jointMap, "Thigh_R");
        this.legR = this.getOrDevException(jointMap, "Leg_R");
        this.kneeR = this.getOrDevException(jointMap, "Knee_R");
        this.thighL = this.getOrDevException(jointMap, "Thigh_L");
        this.legL = this.getOrDevException(jointMap, "Leg_L");
        this.kneeL = this.getOrDevException(jointMap, "Knee_L");
        this.torso = this.getOrDevException(jointMap, "Torso");
        this.chest = this.getOrDevException(jointMap, "Chest");
        this.head = this.getOrDevException(jointMap, "Head");
        this.shoulderR = this.getOrDevException(jointMap, "Shoulder_R");
        this.armR = this.getOrDevException(jointMap, "Arm_R");
        this.handR = this.getOrDevException(jointMap, "Hand_R");
        this.elbowR = this.getOrDevException(jointMap, "Elbow_R");
        this.shoulderL = this.getOrDevException(jointMap, "Shoulder_L");
        this.armL = this.getOrDevException(jointMap, "Arm_L");
        this.handL = this.getOrDevException(jointMap, "Hand_L");
        this.elbowL = this.getOrDevException(jointMap, "Elbow_L");
    }

    public Joint getOrDevException(Map<String, Joint> jointMap, String name) {
        if (!jointMap.containsKey(name)) {
            if (EpicAPI.isDeveloper()) {
                throw new AssetLoadingException("Cannot find the joint named " + name + " in " + this.getClass().getSimpleName());
            }
            return Joint.EMPTY;
        }
        return jointMap.get(name);
    }
}
