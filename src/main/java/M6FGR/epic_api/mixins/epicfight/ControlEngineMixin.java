package M6FGR.epic_api.mixins.epicfight;

import M6FGR.epic_api.input.EpicAPIIntputAction;
import M6FGR.epic_api.skills.EpicAPISkillSlots;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Type;
import net.minecraft.client.KeyMapping;
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
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.events.engine.ControlEngine;
import yesman.epicfight.client.input.InputUtils;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.skill.SkillSlot;
import yesman.epicfight.world.entity.eventlistener.SkillCastEvent;

import static yesman.epicfight.client.input.InputUtils.checkInteractionKeyUsable;

@Mixin(value = ControlEngine.class, remap = false)
public abstract class ControlEngineMixin {


    @Shadow protected abstract boolean isCurrentHoldingAction(@NotNull InputAction other);

    @Shadow
    private static void consumeVanillaAttackKeyClicks() {
    }

    @Shadow private LocalPlayer player;

    @Shadow private boolean attackLightPressToggle;

    @Shadow protected abstract void reserveKey(SkillSlot slot, InputAction action);

    @Shadow private boolean weaponInnatePressToggle;

    @Shadow private int weaponInnatePressCounter;

    @Shadow private LocalPlayerPatch playerPatch;

    @Unique
    private void epicAPI$maybeHeavyAttack() {
        ControlEngine controlEngine = ClientEngine.getInstance().controlEngine;
        if (!this.playerPatch.isEpicFightMode() || isCurrentHoldingAction(EpicAPIIntputAction.HEAVY_ATTACK)) {
            return;
        }
        final MinecraftInputAction vanillaAttack = MinecraftInputAction.ATTACK_DESTROY;
        final EpicAPIIntputAction heavyAttack = EpicAPIIntputAction.HEAVY_ATTACK;

        boolean shouldPlayAttackAnimation = this.playerPatch.canPlayAttackAnimation();
        if (vanillaAttack.keyMapping().getKey() == heavyAttack.keyMapping().getKey() && Minecraft.getInstance().hitResult != null && shouldPlayAttackAnimation) {
            consumeVanillaAttackKeyClicks();
        }

        if (shouldPlayAttackAnimation) {
            if (!InputManager.isBoundToSamePhysicalInput(heavyAttack, EpicFightInputAction.SWITCH_MODE)) {
                SkillCastEvent skillCastEvent = this.playerPatch.getSkill(EpicAPISkillSlots.HEAVY_ATTACK).sendCastRequest(this.playerPatch, controlEngine);

                if (skillCastEvent.isExecutable()) {
                    this.player.resetAttackStrengthTicker();
                    this.attackLightPressToggle = false;
                    controlEngine.releaseAllServedKeys();
                } else {
                    if (!this.player.isSpectator()) {
                        this.reserveKey(EpicAPISkillSlots.HEAVY_ATTACK, heavyAttack);
                    }
                }

                controlEngine.lockHotkeys();
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
    private void injectHeavyAttack(CallbackInfo ci) {
        if (this.playerPatch != null) {
            InputManager.triggerOnPress(EpicAPIIntputAction.HEAVY_ATTACK, () ->
                    runKeyboardMouseEvent(EpicAPIIntputAction.HEAVY_ATTACK, this::epicAPI$maybeHeavyAttack));
        }

    }

    @Unique
    private static void runKeyboardMouseEvent(@NotNull InputAction action, @NotNull Runnable handler) {
        KeyMapping keyMapping = action.keyMapping();
        InputConstants.Key key = keyMapping.getKey();
        boolean isMouse = Type.MOUSE == key.getType();
        int mouseButton = isMouse ? key.getValue() : -1;
        if (checkInteractionKeyUsable(mouseButton, keyMapping)) {
            handler.run();
        }

    }
}
