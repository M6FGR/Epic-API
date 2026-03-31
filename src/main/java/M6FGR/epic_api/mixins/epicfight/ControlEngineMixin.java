package M6FGR.epic_api.mixins.epicfight;

import M6FGR.epic_api.api.input.EpicAPIIntputAction;
import M6FGR.epic_api.skills.EpicAPISkillSlots;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.client.input.InputManager;
import yesman.epicfight.api.client.input.action.EpicFightInputAction;
import yesman.epicfight.api.client.input.action.InputAction;
import yesman.epicfight.api.client.input.action.MinecraftInputAction;
import yesman.epicfight.api.event.types.player.SkillCastEvent;
import yesman.epicfight.client.events.engine.ControlEngine;
import yesman.epicfight.client.input.InputUtils;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.skill.SkillSlot;

@Mixin(value = ControlEngine.class, remap = false)
public abstract class ControlEngineMixin {
    @Shadow private LocalPlayerPatch playerpatch;

    @Shadow protected abstract boolean isCurrentHoldingAction(@NotNull InputAction other);

    @Shadow
    private static void consumeVanillaAttackKeyClicks() {
    }

    @Shadow private LocalPlayer player;

    @Shadow private boolean attackLightPressToggle;

    @Shadow protected abstract void reserveKey(SkillSlot slot, InputAction action);

    @Shadow private boolean weaponInnatePressToggle;

    @Shadow private int weaponInnatePressCounter;

    @Unique
    private void maybeHeavyAttack() {
        if (!this.playerpatch.isEpicFightMode() || isCurrentHoldingAction(EpicAPIIntputAction.HEAVY_ATTACK)) {
            return;
        }
        final MinecraftInputAction vanillaAttack = MinecraftInputAction.ATTACK_DESTROY;
        final EpicAPIIntputAction heavyAttack = EpicAPIIntputAction.HEAVY_ATTACK;

        boolean shouldPlayAttackAnimation = this.playerpatch.canPlayAttackAnimation();
        if (vanillaAttack.keyMapping().getKey() == heavyAttack.keyMapping().getKey() && Minecraft.getInstance().hitResult != null && shouldPlayAttackAnimation) {
            consumeVanillaAttackKeyClicks();
        }

        if (shouldPlayAttackAnimation) {
            if (!InputManager.isBoundToSamePhysicalInput(heavyAttack, EpicFightInputAction.WEAPON_INNATE_SKILL)) {
                SkillCastEvent skillCastEvent = this.playerpatch.getSkill(EpicAPISkillSlots.HEAVY_ATTACK).sendCastRequest(this.playerpatch, ControlEngine.getInstance());

                if (skillCastEvent.isExecutable()) {
                    this.player.resetAttackStrengthTicker();
                    this.attackLightPressToggle = false;
                    ControlEngine.getInstance().releaseAllServedKeys();
                } else {
                    if (!this.player.isSpectator()) {
                        this.reserveKey(EpicAPISkillSlots.HEAVY_ATTACK, heavyAttack);
                    }
                }

                ControlEngine.getInstance().lockHotkeys();
                this.attackLightPressToggle = false;
                this.weaponInnatePressToggle = false;
                this.weaponInnatePressCounter = 0;
            } else {
                if (!this.weaponInnatePressToggle) {
                    this.weaponInnatePressToggle = true;
                }
            }
        }
    }
    @Inject(
            at = @At("HEAD"),
            remap = false,
            method = {"handleEpicFightKeyMappings"}
    )
    private void handleEFAKeyMappings(CallbackInfo ci) {
        if (this.playerpatch != null) {
            InputManager.triggerOnPress(EpicAPIIntputAction.HEAVY_ATTACK, () ->
                    InputUtils.runKeyboardMouseEvent(EpicAPIIntputAction.HEAVY_ATTACK, this::maybeHeavyAttack));
        }

    }
}
