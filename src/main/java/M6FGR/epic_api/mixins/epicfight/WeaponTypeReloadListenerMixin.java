package M6FGR.epic_api.mixins.epicfight;
import M6FGR.epic_api.builders.epicfight.WeaponCapabilityBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.world.capabilities.item.WeaponCapability.Builder;
import yesman.epicfight.world.capabilities.item.WeaponTypeReloadListener;
// deprecated!

@Mixin(value = WeaponTypeReloadListener.class, remap = false, priority = 1001)
public abstract class WeaponTypeReloadListenerMixin {

    // Changed to HEAD and avoided the deprecated method
    @Inject(
            method = "deserializeWeaponCapabilityBuilder(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/nbt/CompoundTag;)Lyesman/epicfight/world/capabilities/item/WeaponCapability$Builder;",
            at = @At("HEAD"),
            remap = false
    )
    private static void injectHeavyCombos(ResourceLocation rl, CompoundTag tag, CallbackInfoReturnable<Builder> cir) {
        // make HeavyAttack and CounterAttack datapack friendly too!
        WeaponCapabilityBuilder.registerHeavyComboFromTag(rl, tag);
        WeaponCapabilityBuilder.registerParryCounterFromTag(rl, tag);
        WeaponCapabilityBuilder.registerCounterAttackFromTag(rl, tag);
    }
}
