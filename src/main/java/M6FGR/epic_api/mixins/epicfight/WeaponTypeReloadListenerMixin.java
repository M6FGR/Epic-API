package M6FGR.epic_api.mixins.epicfight;

import M6FGR.epic_api.api.registry.WeaponCapabilityRegistry;
import M6FGR.epic_api.main.EpicAPI;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.world.capabilities.item.Style;
import yesman.epicfight.world.capabilities.item.WeaponCapability;
import yesman.epicfight.world.capabilities.item.WeaponCategory;
import yesman.epicfight.world.capabilities.item.WeaponTypeReloadListener;
import yesman.epicfight.world.capabilities.provider.ExtraEntryProvider;

import java.util.ArrayList;
import java.util.List;
// make it datapack friendly too!
@Mixin(value = WeaponTypeReloadListener.class, remap = false)
@SuppressWarnings("removal")
public class WeaponTypeReloadListenerMixin {

    @Inject(
            method = "deserializeWeaponCapabilityBuilder(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/nbt/CompoundTag;Lyesman/epicfight/world/capabilities/provider/ExtraEntryProvider;)Lyesman/epicfight/world/capabilities/item/WeaponCapability$Builder;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;getCompound(Ljava/lang/String;)Lnet/minecraft/nbt/CompoundTag;", ordinal = 1), // Targets the 'innate_skills' getCompound call
            remap = false
    )
    private static void injectHeavyCombos(ResourceLocation rl, CompoundTag tag, ExtraEntryProvider extraEntryProvider, CallbackInfoReturnable<WeaponCapability.Builder> cir) {
        if (tag.contains("heavy_combos")) {
            try {
                String categoryStr = tag.getString("category");
                WeaponCategory category = WeaponCategory.ENUM_MANAGER.getOrThrow(categoryStr);
                CompoundTag heavyCombosTag = tag.getCompound("heavy_combos");

                for (String key : heavyCombosTag.getAllKeys()) {
                    Style style = Style.ENUM_MANAGER.getOrThrow(key);
                    ListTag comboAnimations = heavyCombosTag.getList(key, 8);
                    List<AnimationManager.AnimationAccessor<? extends AttackAnimation>> anims = new ArrayList<>();

                    for (int i = 0; i < comboAnimations.size(); ++i) {
                        String animId = comboAnimations.getString(i);
                        AnimationManager.AnimationAccessor<? extends AttackAnimation> animation =
                                (extraEntryProvider == null) ? AnimationManager.byKey(animId) : extraEntryProvider.getExtraOrBuiltInAnimation(animId);

                        if (animation != null) {
                            anims.add(animation);
                        }
                    }

                    if (!anims.isEmpty()) {
                        WeaponCapabilityRegistry.builder().registerHeavyComboFromTag(category, style, anims);
                    }
                }
            } catch (Exception e) {
                EpicAPI.LOGGER.error("Failed to inject heavy combos for " + rl, e);
            }
        }
    }
}
