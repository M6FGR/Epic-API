package dev.m6fgr.epic_api.mixins.epicfight.common;

import dev.m6fgr.epic_api.events.epic_api.skill.AddGuardMotionsDispatchableEvent;
import dev.m6fgr.epic_api.events.epic_api.skill.AddGuardMotionsDispatchableEvent.Parry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.skill.guard.GuardSkill.Builder;
import yesman.epicfight.skill.guard.ParryingSkill;

@Mixin(value = ParryingSkill.class, remap = false, priority = 1001)
public class ParrySkillMixin {


    @Inject(
            at = @At("TAIL"),
            method = "createActiveGuardBuilder",
            remap = false
    )

    private static void injectEvent(CallbackInfoReturnable<Builder> cir) {
        AddGuardMotionsDispatchableEvent.Parry parryEH = new Parry(cir.getReturnValue());
        parryEH.post();
    }

}
