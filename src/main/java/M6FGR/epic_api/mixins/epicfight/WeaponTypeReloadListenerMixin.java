package M6FGR.epic_api.mixins.epicfight;

import M6FGR.epic_api.builders.epicfight.excap.deferred.DeferredCapabilityBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.world.capabilities.item.WeaponCapability;
import yesman.epicfight.world.capabilities.item.WeaponTypeReloadListener;
import yesman.epicfight.world.capabilities.provider.ExtraEntryProvider;
@Mixin(
        value = WeaponTypeReloadListener.class,
        remap = false
)
public class WeaponTypeReloadListenerMixin {

    @Inject(
            method = "deserializeWeaponCapabilityBuilder(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/nbt/CompoundTag;Lyesman/epicfight/world/capabilities/provider/ExtraEntryProvider;)Lyesman/epicfight/world/capabilities/item/WeaponCapability$Builder;",
            at = @At(value = "TAIL"),
            remap = false
    )
    private static void epicAPI$injectHeavyCombos(ResourceLocation rl, CompoundTag tag, ExtraEntryProvider extraEntryProvider, CallbackInfoReturnable<WeaponCapability.Builder> cir) {
        // make HeavyAttack and CounterAttack datapack friendly too!
        DeferredCapabilityBuilder.registerHeavyComboFromTag(rl, tag); // removed ExtraEntryProvider usage, it's deprecated!
        DeferredCapabilityBuilder.registerCounterFromTag(rl, tag); // removed ExtraEntryProvider usage, it's deprecated!
    }
}
