package dev.m6fgr.epic_api.animation.types.joints;

import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.client.animation.property.JointMaskEntry;
import yesman.epicfight.api.utils.ExtensibleEnum;
import yesman.epicfight.api.utils.ExtensibleEnumManager;

public interface JointMask extends ExtensibleEnum {

    ExtensibleEnumManager<JointMask> ENUM_MANAGER = new ExtensibleEnumManager<>("joint_mask");

    Joint[] getJoints();

    JointMaskEntry getAsEntry();

}
