package M6FGR.epic_api.models.armature;

import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.model.Armature;

import java.util.Map;
// same as HumanoidArmature but with no tools, just a placeholder
public class NonHumanoidArmature extends Armature {
    public final Joint thighR;
    public final Joint legR;
    public final Joint kneeR;
    public final Joint thighL;
    public final Joint legL;
    public final Joint kneeL;
    public final Joint torso;
    public final Joint chest;
    public final Joint head;
    public final Joint shoulderR;
    public final Joint armR;
    public final Joint handR;
    public final Joint elbowR;
    public final Joint shoulderL;
    public final Joint armL;
    public final Joint handL;
    public final Joint elbowL;

    public NonHumanoidArmature(String name, int jointNumber, Joint rootJoint, Map<String, Joint> jointMap) {
        super(name, jointNumber, rootJoint, jointMap);
        this.thighR = this.getOrLogException(jointMap, "Thigh_R");
        this.legR = this.getOrLogException(jointMap, "Leg_R");
        this.kneeR = this.getOrLogException(jointMap, "Knee_R");
        this.thighL = this.getOrLogException(jointMap, "Thigh_L");
        this.legL = this.getOrLogException(jointMap, "Leg_L");
        this.kneeL = this.getOrLogException(jointMap, "Knee_L");
        this.torso = this.getOrLogException(jointMap, "Torso");
        this.chest = this.getOrLogException(jointMap, "Chest");
        this.head = this.getOrLogException(jointMap, "Head");
        this.shoulderR = this.getOrLogException(jointMap, "Shoulder_R");
        this.armR = this.getOrLogException(jointMap, "Arm_R");
        this.handR = this.getOrLogException(jointMap, "Hand_R");
        this.elbowR = this.getOrLogException(jointMap, "Elbow_R");
        this.shoulderL = this.getOrLogException(jointMap, "Shoulder_L");
        this.armL = this.getOrLogException(jointMap, "Arm_L");
        this.handL = this.getOrLogException(jointMap, "Hand_L");
        this.elbowL = this.getOrLogException(jointMap, "Elbow_L");
    }

}
