package M6FGR.epic_api.world.capabilities.item;

import M6FGR.epic_api.api.cls.ILoadableClass;
import M6FGR.epic_api.api.events.hooks.EpicAPIEventHooks;
import M6FGR.epic_api.api.registry.MoveSetRegistry;
import M6FGR.epic_api.api.registry.WeaponCapabilityRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
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
// just a placeholder of how the API works with heavy attacks
public class WeaponCapabilityPresets implements ILoadableClass {
    // EpicFight Registry
    public static final Function<Item, WeaponCapability.Builder> LONGSWORD = item ->
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
            event.getTypeEntry().put(ResourceLocation.fromNamespaceAndPath("epicfight", "longsword"), LONGSWORD);
        });
    }
    // making is static final will cause a NullPointerException!, initialize it inside the registry method!
    private MoveSetRegistry BOKKEN_EX_CAP;

    private void registerMoveSet() {
        this.BOKKEN_EX_CAP = MoveSetRegistry.builder()
                .newMoveSet(
                        Styles.TWO_HAND,
                        EpicFight.identifier("bokken_2h"),
                        WeaponCategories.LONGSWORD,
                        ColliderPreset.LONGSWORD,
                        EpicFightSounds.WHOOSH_ROD.get(),
                        EpicFightSounds.BLADE_HIT.get(),
                        EpicFightParticles.HIT_BLADE.get(),
                        false,
                        null,
                        EpicFightSkills.LIECHTENAUER.get(),
                        MainConditionals.DEFAULT_2H_WIELD_STYLE,
                        Animations.LONGSWORD_AUTO1,
                        Animations.LONGSWORD_AUTO2,
                        Animations.LONGSWORD_AUTO3,
                        Animations.LONGSWORD_DASH,
                        Animations.LONGSWORD_AIR_SLASH
                )
                .forEachMotion(
                        LivingMotions.IDLE, Animations.BIPED_HOLD_LONGSWORD,
                        LivingMotions.WALK, Animations.BIPED_WALK_LONGSWORD,
                        LivingMotions.RUN, Animations.BIPED_RUN_LONGSWORD,
                        LivingMotions.BLOCK, Animations.LONGSWORD_GUARD
                )
                // child gets the motions from above
                .withChild(
                        Styles.ONE_HAND,
                        EpicFight.identifier("bokken_1h"),
                        EpicFightSkills.SHARP_STAB.get(),
                        MainConditionals.SHIELD_OFFHAND
                )
                .withHeavyCombo(
                        Styles.TWO_HAND,
                        Animations.LONGSWORD_LIECHTENAUER_AUTO1,
                        Animations.LONGSWORD_LIECHTENAUER_AUTO2,
                        Animations.LONGSWORD_LIECHTENAUER_AUTO3,
                        Animations.LONGSWORD_DASH,
                        Animations.LONGSWORD_AIR_SLASH
                )
                .withChildMoveSet(
                        Styles.OCHS,
                        EpicFight.identifier("bokken_liechtenauer"),
                        null,
                        EpicFightSkills.LIECHTENAUER.get(),
                        MainConditionals.LIECHTENAUER_CONDITION,
                        Animations.LONGSWORD_LIECHTENAUER_AUTO1,
                        Animations.LONGSWORD_LIECHTENAUER_AUTO2,
                        Animations.LONGSWORD_LIECHTENAUER_AUTO3,
                        Animations.LONGSWORD_DASH,
                        Animations.LONGSWORD_AIR_SLASH
                )
                .forEachMotion(
                        LivingMotions.IDLE, Animations.BIPED_HOLD_LIECHTENAUER,
                        LivingMotions.WALK, Animations.BIPED_WALK_LIECHTENAUER,
                        LivingMotions.ALL, Animations.BIPED_HOLD_LIECHTENAUER
                );
        EpicAPIEventHooks.Registry.EX_CAP_CAPABILITY_REGISTRY.registerEvent(event -> {
            event.register(ResourceLocation.fromNamespaceAndPath("epicfight", "bokken"), this.BOKKEN_EX_CAP);
        });
    }

    @Override
    public void onModCommonEvents(FMLCommonSetupEvent commonEvent) {
        this.registerCapabilities();
        this.registerMoveSet();
    }
}
