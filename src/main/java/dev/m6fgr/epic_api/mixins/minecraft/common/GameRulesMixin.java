package dev.m6fgr.epic_api.mixins.minecraft.common;

import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = GameRules.class, remap = false, priority = 1005)
public class GameRulesMixin {

    @Inject(
            at = @At("TAIL"),
            method = "<init>(Ljava/util/Map;)V",
            remap = false
    )
    private void injectDispatchableEvent(Map<GameRules.Key<?>, GameRules.Value<?>> rules, CallbackInfo ci) {

    }


}
