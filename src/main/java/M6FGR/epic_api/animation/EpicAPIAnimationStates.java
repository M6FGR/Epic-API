package M6FGR.epic_api.animation;

import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.utils.datastruct.TypeFlexibleHashMap;

public class EpicAPIAnimationStates extends EntityState {

    public static final StateFactor<Boolean> CAN_COUNTER = new StateFactor<>("canCounter", false);


    public EpicAPIAnimationStates(TypeFlexibleHashMap<StateFactor<?>> states) {
        super(states);
    }
}
