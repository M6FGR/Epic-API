package M6FGR.epic_api.mixins.epicfight;

import M6FGR.epic_api.api.registry.WeaponCapabilityRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.world.capabilities.item.WeaponCapability;
import yesman.epicfight.world.capabilities.item.WeaponTypeReloadListener;
// deprecated!
import yesman.epicfight.world.capabilities.provider.ExtraEntryProvider;
@Mixin(value = WeaponTypeReloadListener.class, remap = false)
@SuppressWarnings("removal")
public class WeaponTypeReloadListenerMixin {

    @Inject(
            method = "deserializeWeaponCapabilityBuilder(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/nbt/CompoundTag;Lyesman/epicfight/world/capabilities/provider/ExtraEntryProvider;)Lyesman/epicfight/world/capabilities/item/WeaponCapability$Builder;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;getCompound(Ljava/lang/String;)Lnet/minecraft/nbt/CompoundTag;", ordinal = 1), // Targets the 'innate_skills' getCompound call
            remap = false
    )
    private static void injectHeavyCombos(ResourceLocation rl, CompoundTag tag, ExtraEntryProvider extraEntryProvider, CallbackInfoReturnable<WeaponCapability.Builder> cir) {
        // make HeavyAttack datapack friendly too!
        WeaponCapabilityRegistry.builder().registerHeavyComboFromTag(rl, tag, extraEntryProvider);
    }
}
