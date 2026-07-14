package M6FGR.epic_api.mixins.epicfight.common;

import M6FGR.epic_api.animation.EpicAPIAnimationStates;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.GuardAnimation;
import yesman.epicfight.api.animation.types.StateSpectrum.Blueprint;

@Mixin(value = GuardAnimation.class, remap = false)
public abstract class GuardAnimationMixin {
    @WrapOperation(
            method = "<init>(FFLyesman/epicfight/api/animation/AnimationManager$AnimationAccessor;Lyesman/epicfight/api/asset/AssetAccessor;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lyesman/epicfight/api/animation/types/StateSpectrum$Blueprint;newTimePair(FF)Lyesman/epicfight/api/animation/types/StateSpectrum$Blueprint;"
            )
    )
    private Blueprint injectCounterState(Blueprint instance, float start, float end, Operation<Blueprint> original) {
        return original.call(instance, start, end).newTimePair(start, end)
                .newTimePair(start, end)
                .addState(EntityState.TURNING_LOCKED, true)
                .addState(EntityState.MOVEMENT_LOCKED, true)
                .addState(EntityState.UPDATE_LIVING_MOTION, false)
                .addState(EntityState.COMBO_ATTACKS_DOABLE, false)
                .addState(EpicAPIAnimationStates.CAN_COUNTER, true)
                .newTimePair(start, Float.MAX_VALUE)
                .addState(EntityState.INACTION, true);
    }
}