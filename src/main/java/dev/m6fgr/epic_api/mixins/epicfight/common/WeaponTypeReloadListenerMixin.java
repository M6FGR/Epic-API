package dev.m6fgr.epic_api.mixins.epicfight.common;

import dev.m6fgr.epic_api.builders.epicfight.excap.deferred.DeferredCapabilityBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.world.capabilities.item.WeaponCapability;
import yesman.epicfight.world.capabilities.item.WeaponTypeReloadListener;
@Mixin(
        value = WeaponTypeReloadListener.class,
        remap = false
)
public class WeaponTypeReloadListenerMixin {

    @Inject(
            method = "deserializeWeaponCapabilityBuilder(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/nbt/CompoundTag;)Lyesman/epicfight/world/capabilities/item/WeaponCapability$Builder;",
            at = @At(value = "RETURN"),
            remap = false
    )
    private static void epicAPI$injectHeavyCombos(ResourceLocation rl, CompoundTag tag, CallbackInfoReturnable<WeaponCapability.Builder> cir) {
        // make HeavyAttack and CounterAttack datapack friendly too!
        DeferredCapabilityBuilder.registerHeavyComboFromTag(rl, tag);
        DeferredCapabilityBuilder.registerCounterFromTag(rl, tag);
    }
}
