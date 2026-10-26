package dev.m6fgr.epic_api.animation;

import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.utils.datastructure.ParameterizedHashMap;

public class EpicAPIAnimationStates extends EntityState {

    public static final StateFactor<Boolean> CAN_COUNTER = new StateFactor<>("canCounter", false);

    public EpicAPIAnimationStates(ParameterizedHashMap<StateFactor<?>> states) {
        super(states);
    }
}
