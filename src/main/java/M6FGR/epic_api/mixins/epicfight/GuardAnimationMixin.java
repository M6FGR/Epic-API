package M6FGR.epic_api.mixins.epicfight;

import M6FGR.epic_api.animation.EpicAPIAnimationStates;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import yesman.epicfight.api.animation.types.GuardAnimation;
import yesman.epicfight.api.animation.types.StateSpectrum.Blueprint;

@Mixin(value = GuardAnimation.class, remap = false)
public abstract class GuardAnimationMixin {
    @Redirect(
            method = "<init>(FFLyesman/epicfight/api/animation/AnimationManager$AnimationAccessor;Lyesman/epicfight/api/asset/AssetAccessor;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lyesman/epicfight/api/animation/types/StateSpectrum$Blueprint;newTimePair(FF)Lyesman/epicfight/api/animation/types/StateSpectrum$Blueprint;"
            )
    )
    private Blueprint epicAPI$canCounterState(Blueprint instance, float start, float end) {
        return instance.newTimePair(start, end)
                .addState(EpicAPIAnimationStates.CAN_COUNTER, true);
    }
}