package M6FGR.epic_api.world.capabilities.item;

import M6FGR.epic_api.api.events.hooks.EpicAPIEventHooks;
import M6FGR.epic_api.api.registry.MoveSetRegistry;
import M6FGR.epic_api.api.registry.WeaponCapabilityRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import yesman.epicfight.EpicFight;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.event.EpicFightEventHooks;
import yesman.epicfight.api.ex_cap.modules.assets.MainConditionals;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.ColliderPreset;
import yesman.epicfight.registry.entries.EpicFightParticles;
import yesman.epicfight.registry.entries.EpicFightSkills;
import yesman.epicfight.registry.entries.EpicFightSounds;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem.Styles;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories;
import yesman.epicfight.world.capabilities.item.WeaponCapability;

import java.util.function.Function;
// Note that those presets below are examples!, not suggested to use them in an actual addon
public class EpicAPIPresets {
    // EpicFight Registry
    public static final Function<Item, WeaponCapability.Builder> EXAMPLE_EFM = item ->
            WeaponCapabilityRegistry.builder()
                    .withStyleConditions(entityPatch -> {
                        if (entityPatch.getHoldingItemCapability(InteractionHand.OFF_HAND).getWeaponCategory() == WeaponCategories.SHIELD) {
                            return Styles.ONE_HAND;
                        } else if (entityPatch instanceof PlayerPatch<?> playerPatch) {
                            if (playerPatch.getSkill(SkillSlots.WEAPON_INNATE).isActivated()) {
                                return Styles.OCHS;
                            }
                        }
                        return Styles.TWO_HAND;
                    })
                    // Two-handed preset
                    .newPreset(
                            Styles.TWO_HAND,
                            WeaponCategories.LONGSWORD,
                            ColliderPreset.LONGSWORD,
                            EpicFightSounds.WHOOSH.get(),
                            EpicFightSounds.BLADE_HIT.get(),
                            EpicFightParticles.HIT_BLADE.get(),
                            false,
                            null,
                            EpicFightSkills.LIECHTENAUER.get(),
                            Animations.LONGSWORD_AUTO1,
                            Animations.LONGSWORD_AUTO2,
                            Animations.LONGSWORD_AUTO3,
                            Animations.LONGSWORD_DASH,
                            Animations.LONGSWORD_AIR_SLASH
                    )
                    .withNewHeavyCombo(
                            Animations.LONGSWORD_LIECHTENAUER_AUTO1,
                            Animations.LONGSWORD_LIECHTENAUER_AUTO2,
                            Animations.LONGSWORD_LIECHTENAUER_AUTO3,
                            Animations.LONGSWORD_DASH,
                            Animations.LONGSWORD_AIR_SLASH
                    )
                    .forEachMotion(
                            LivingMotions.IDLE, Animations.BIPED_HOLD_LONGSWORD,
                            LivingMotions.WALK, Animations.BIPED_WALK_LONGSWORD,
                            LivingMotions.RUN, Animations.BIPED_RUN_LONGSWORD,
                            LivingMotions.BLOCK, Animations.LONGSWORD_GUARD
                    )
                    // One-handed preset
                    .secondaryStyle(
                            Styles.ONE_HAND,
                            null,
                            EpicFightSkills.SHARP_STAB.get()
                    )
                    // Innate skill preset (Ochs)
                    .secondaryPreset(
                            Styles.OCHS,
                            null,
                            EpicFightSkills.LIECHTENAUER.get(),
                            Animations.LONGSWORD_LIECHTENAUER_AUTO1,
                            Animations.LONGSWORD_LIECHTENAUER_AUTO2,
                            Animations.LONGSWORD_LIECHTENAUER_AUTO3,
                            Animations.LONGSWORD_DASH,
                            Animations.LONGSWORD_AIR_SLASH
                    )
                    .forEachMotion(
                            Styles.OCHS,
                            LivingMotions.IDLE, Animations.BIPED_HOLD_LIECHTENAUER,
                            LivingMotions.WALK, Animations.BIPED_WALK_LIECHTENAUER,
                            LivingMotions.ALL, Animations.BIPED_HOLD_LIECHTENAUER
                    )
                    .build();


    private void registerCapabilities() {
        EpicFightEventHooks.Registry.WEAPON_CAPABILITY_PRESET.registerEvent(event -> {
            event.getTypeEntry().put(ResourceLocation.fromNamespaceAndPath("epic_api", "example_efm"), EXAMPLE_EFM);
        });
    }
    // making is static final will cause a NullPointerException!, initialize it inside the registry method!
    private MoveSetRegistry EXAMPLE_EXCAP;

    private void registerMoveSet() {
        this.EXAMPLE_EXCAP = MoveSetRegistry.builder()
                .newMoveSet(
                        Styles.TWO_HAND,
                        EpicFight.identifier("example_2h"),
                        WeaponCategories.LONGSWORD,
                        ColliderPreset.LONGSWORD,
                        EpicFightSounds.WHOOSH_ROD.get(),
                        EpicFightSounds.BLUNT_HIT_HARD.get(),
                        EpicFightParticles.HIT_BLUNT.get(),
                        false,
                        null,
                        EpicFightSkills.SWEEPING_EDGE.get(),
                        MainConditionals.DEFAULT_2H_WIELD_STYLE,
                        Animations.SWORD_AUTO1,
                        Animations.SWORD_AUTO2,
                        Animations.SWORD_DASH,
                        Animations.SWORD_AIR_SLASH
                )
                .withHeavyCombo(
                        Styles.TWO_HAND,
                        Animations.LONGSWORD_LIECHTENAUER_AUTO1,
                        Animations.LONGSWORD_LIECHTENAUER_AUTO2,
                        Animations.LONGSWORD_LIECHTENAUER_AUTO3,
                        Animations.LONGSWORD_DASH,
                        Animations.LONGSWORD_AIR_SLASH
                )
                .forEachMotion(
                        LivingMotions.IDLE, Animations.BIPED_IDLE,
                        LivingMotions.WALK, Animations.BIPED_WALK,
                        LivingMotions.RUN, Animations.BIPED_RUN_LONGSWORD
                )
                // child gets the motions from above
                .withChildMoveSet(
                        Styles.ONE_HAND,
                        EpicFight.identifier("example_1h"),
                        EpicFightSkills.HEARTPIERCER.get(),
                        null,
                        MainConditionals.SHIELD_OFFHAND,
                        Animations.SWORD_AUTO1,
                        Animations.SWORD_AUTO2,
                        Animations.SWORD_AUTO3,
                        Animations.SWORD_DASH,
                        Animations.SWORD_AIR_SLASH
                ).withDefaultBipedMotions();
        EpicAPIEventHooks.Registry.EX_CAP_CAPABILITY_REGISTRY.registerEvent(event -> {
            event.register(ResourceLocation.fromNamespaceAndPath("epic_api", "example_excap"), this.EXAMPLE_EXCAP);
        });
    }
}
