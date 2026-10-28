package dev.m6fgr.epic_api.client.gui;

import dev.m6fgr.epic_api.client.gui.data.DialogueData;
import dev.m6fgr.epic_api.client.gui.data.OptionStyle;
import dev.m6fgr.epic_api.client.gui.screens.InteractionMiniScreen;
import dev.m6fgr.epic_api.events.epic_api.entity.EntityInteractDispatchableEvent;
import dev.m6fgr.epic_api.network.EpicAPINetworkManager;
import dev.m6fgr.epic_api.network.packets.client.CPFreezeEntity;
import dev.m6fgr.epic_api.utils.code.MathUtil;
import dev.m6fgr.epic_api.utils.color.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class InteractionAPI {

    private static final InteractionAPI INSTANCE = new InteractionAPI();

    private boolean interacting;
    private Entity currentTarget;
    private double maxInteractionDistanceSqr = 36.0;

    private static final float ROTATION_SPEED = 12.0F;

    private float lookAtPlayerDuration = 3.0F;
    private float lookTimer = 0.0F;

    private double lockedX;
    private double lockedY;
    private double lockedZ;
    private float lockedYaw;
    private float lockedPitch;
    private float lockedBodyYaw;

    private InteractionMiniScreen interactionMiniScreen;
    private DialogueData.Builder pendingDialogueBuilder;

    private Integer pendingWidgetColor = null;
    private Integer pendingWidgetOutlineColor = null;
    private Integer pendingEntityNameColor = null;
    private Integer pendingTextColor = null;

    private final List<Predicate<Entity>> interactionFilters = new ArrayList<>();

    public static InteractionAPI getInstance() {
        return INSTANCE;
    }

    private InteractionAPI() {
        this.addFilter(entity -> entity instanceof LivingEntity && entity.isAlive());
    }

    public void interactWithEntity(Player interacter, Entity interactedEntity) {
        if (!(interacter instanceof LocalPlayer localPlayer)) {
            return;
        }

        if (localPlayer.distanceToSqr(interactedEntity) > this.maxInteractionDistanceSqr) {
            localPlayer.displayClientMessage(Component.literal("Target is too far away!"), true);
            return;
        }

        if (!this.canInteractWith(interactedEntity)) {
            Minecraft.getInstance().gui.setOverlayMessage(
                    Component.literal("Entity " + interactedEntity.getDisplayName().getString() + " cannot speak!"),
                    false
            );
            return;
        }

        this.clearPendingColorBuffers();
        this.pendingDialogueBuilder = DialogueData.builder(interactedEntity.getDisplayName(), "...");

        EntityInteractDispatchableEvent entityInteractDE = new EntityInteractDispatchableEvent(interacter, interactedEntity, this);
        entityInteractDE.post();

        if (entityInteractDE.isCanceled()) {
            this.pendingDialogueBuilder = null;
            this.clearPendingColorBuffers();
            return;
        }

        DialogueData finalDialogue = this.pendingDialogueBuilder.build();
        if (finalDialogue.getOptions().isEmpty()) {
            this.pendingDialogueBuilder.addOption("Close", InteractionType.NONE);
            finalDialogue = this.pendingDialogueBuilder.build();
        }

        this.pendingDialogueBuilder = null;

        Entity target = entityInteractDE.getSpeaker();
        Minecraft mc = Minecraft.getInstance();
        InteractionMiniScreen screen = new InteractionMiniScreen(target, finalDialogue, finalDialogue.getOptionStyle());

        this.applyPendingColorsToScreen(screen);

        this.interactionMiniScreen = screen;
        mc.setScreen(screen);
        this.startInteraction(target);
    }

    public void interactWithEntity(Player interacter, Entity interactedEntity, DialogueData dialogueData) {
        if (!(interacter instanceof LocalPlayer localPlayer)) {
            return;
        }

        if (localPlayer.distanceToSqr(interactedEntity) > this.maxInteractionDistanceSqr) {
            localPlayer.displayClientMessage(Component.literal("Target is too far away!"), true);
            return;
        }

        if (!this.canInteractWith(interactedEntity)) {
            Minecraft.getInstance().gui.setOverlayMessage(
                    Component.literal("Entity " + interactedEntity.getDisplayName().getString() + " cannot speak!"),
                    false
            );
            return;
        }

        this.clearPendingColorBuffers();

        this.pendingDialogueBuilder = DialogueData.builder(dialogueData.getSpeakerName(), dialogueData.getCurrentText())
                .charsPerTick(dialogueData.getCharsPerTick())
                .style(dialogueData.getOptionStyle());

        for (DialogueData.DialogueOption opt : dialogueData.getOptions()) {
            this.pendingDialogueBuilder.addOption(opt.label(), opt.interactionType(), opt.clientCallback());
        }

        EntityInteractDispatchableEvent entityInteractDE = new EntityInteractDispatchableEvent(interacter, interactedEntity, this);
        entityInteractDE.post();

        if (entityInteractDE.isCanceled()) {
            this.pendingDialogueBuilder = null;
            this.clearPendingColorBuffers();
            return;
        }

        Entity target = entityInteractDE.getSpeaker();
        DialogueData finalDialogue = this.pendingDialogueBuilder.build();
        this.pendingDialogueBuilder = null;

        Minecraft mc = Minecraft.getInstance();
        InteractionMiniScreen screen = new InteractionMiniScreen(target, finalDialogue, finalDialogue.getOptionStyle());

        this.applyPendingColorsToScreen(screen);

        this.interactionMiniScreen = screen;
        mc.setScreen(screen);
        this.startInteraction(target);
    }

    public InteractionAPI setSpeakerName(Component speakerName) {
        if (this.pendingDialogueBuilder != null) {
            this.pendingDialogueBuilder.speakerName(speakerName);
        }
        return this;
    }

    public InteractionAPI setDialogueText(String text) {
        if (this.pendingDialogueBuilder != null) {
            this.pendingDialogueBuilder.text(text);
        }
        return this;
    }

    public InteractionAPI setCharsPerTick(int charsPerTick) {
        if (this.pendingDialogueBuilder != null) {
            this.pendingDialogueBuilder.charsPerTick(charsPerTick);
        }
        return this;
    }

    public InteractionAPI setOptionStyle(OptionStyle optionStyle) {
        if (this.pendingDialogueBuilder != null) {
            this.pendingDialogueBuilder.style(optionStyle);
        }
        return this;
    }

    public InteractionAPI addOption(Component label, InteractionType interactionType, Runnable clientCallback) {
        if (this.pendingDialogueBuilder != null) {
            this.pendingDialogueBuilder.addOption(label, interactionType, clientCallback);
        }
        return this;
    }

    public InteractionAPI addPage(String pageText) {
        if (this.pendingDialogueBuilder != null) {
            this.pendingDialogueBuilder.addPage(pageText);
        }
        return this;
    }

    public InteractionAPI clearOptions() {
        if (this.pendingDialogueBuilder != null) {
            this.pendingDialogueBuilder.clearOptions();
        }
        return this;
    }

    public InteractionAPI setEntityNameColor(int red, int green, int blue, int transparency) {
        return this.setEntityNameColor(MathUtil.toHex(red, green, blue, transparency));
    }

    public InteractionAPI setEntityNameColor(int hexColor) {
        this.pendingEntityNameColor = hexColor;
        if (this.interactionMiniScreen != null) {
            this.interactionMiniScreen.setEntityNameColor(hexColor);
        }
        return this;
    }

    public InteractionAPI setInteractionScreenColor(int red, int green, int blue, int transparency) {
        return this.setInteractionScreenColor(MathUtil.toHex(red, green, blue, transparency));
    }

    public InteractionAPI setInteractionScreenColor(int hexColor) {
        this.pendingWidgetColor = hexColor;
        if (this.interactionMiniScreen != null) {
            this.interactionMiniScreen.setWidgetColor(hexColor);
        }
        return this;
    }

    public InteractionAPI setInteractionScreenOutlineColor(int red, int green, int blue, int transparency) {
        return this.setInteractionScreenOutlineColor(MathUtil.toHex(red, green, blue, transparency));
    }

    public InteractionAPI setInteractionScreenOutlineColor(int hexColor) {
        this.pendingWidgetOutlineColor = hexColor;
        if (this.interactionMiniScreen != null) {
            this.interactionMiniScreen.setWidgetOutlineColor(hexColor);
        }
        return this;
    }

    public InteractionAPI setTextColor(int red, int green, int blue, int transparency) {
        return this.setTextColor(MathUtil.toHex(red, green, blue, transparency));
    }

    public InteractionAPI setTextColor(int hexColor) {
        this.pendingTextColor = hexColor;
        if (this.interactionMiniScreen != null) {
            this.interactionMiniScreen.setTextColor(hexColor);
        }
        return this;
    }

    public InteractionAPI setEntityNameColor(Color color) {
        return this.setEntityNameColor(color.getARGB());
    }

    public InteractionAPI setInteractionScreenColor(Color color) {
        return this.setInteractionScreenColor(color.getARGB());
    }

    public InteractionAPI setInteractionScreenOutlineColor(Color color) {
        return this.setInteractionScreenOutlineColor(color.getARGB());
    }

    public InteractionAPI setTextColor(Color color) {
        return this.setTextColor(color.getARGB());
    }

    private void applyPendingColorsToScreen(InteractionMiniScreen screen) {
        if (this.pendingEntityNameColor != null) {
            screen.setEntityNameColor(this.pendingEntityNameColor);
        }
        if (this.pendingTextColor != null) {
            screen.setTextColor(this.pendingTextColor);
        }
        if (this.pendingWidgetColor != null) {
            screen.setWidgetColor(this.pendingWidgetColor);
        }
        if (this.pendingWidgetOutlineColor != null) {
            screen.setWidgetOutlineColor(this.pendingWidgetOutlineColor);
        }
        this.clearPendingColorBuffers();
    }

    private void clearPendingColorBuffers() {
        this.pendingEntityNameColor = null;
        this.pendingTextColor = null;
        this.pendingWidgetColor = null;
        this.pendingWidgetOutlineColor = null;
    }

    public InteractionAPI startInteraction(Entity target) {
        this.interacting = true;
        this.currentTarget = target;
        this.lookTimer = 0.0F;

        this.lockedX = target.getX();
        this.lockedY = target.getY();
        this.lockedZ = target.getZ();
        this.lockedYaw = target.getYRot();
        this.lockedPitch = target.getXRot();

        if (target instanceof LivingEntity living) {
            this.lockedBodyYaw = living.yBodyRot;
        }

        this.freezeEntity();

        return this;
    }

    public void updateInteractionLock(float deltaSeconds) {
        if (!this.interacting) {
            return;
        }

        float clampedDelta = Math.min(deltaSeconds, 0.1F);
        this.renderInteractionLockDelta(clampedDelta);
    }

    private void renderInteractionLockDelta(float deltaSeconds) {
        if (!this.interacting || this.currentTarget == null || !this.currentTarget.isAlive()) {
            this.closeInteraction();
            return;
        }

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        if (player.distanceToSqr(this.currentTarget) > this.maxInteractionDistanceSqr) {
            this.closeInteraction();
            return;
        }

        Vec3 playerVel = player.getDeltaMovement();
        player.setDeltaMovement(0.0, playerVel.y, 0.0);

        this.currentTarget.setPos(this.lockedX, this.lockedY, this.lockedZ);
        this.currentTarget.xo = this.lockedX;
        this.currentTarget.yo = this.lockedY;
        this.currentTarget.zo = this.lockedZ;
        this.currentTarget.xOld = this.lockedX;
        this.currentTarget.yOld = this.lockedY;
        this.currentTarget.zOld = this.lockedZ;

        this.currentTarget.setDeltaMovement(Vec3.ZERO);

        if (this.currentTarget instanceof Mob mobTarget) {
            mobTarget.stopInPlace();
            mobTarget.getNavigation().stop();
            mobTarget.setSpeed(0.0F);
            mobTarget.xxa = 0.0F;
            mobTarget.yya = 0.0F;
            mobTarget.zza = 0.0F;
        }

        Vec3 playerEyePos = player.getEyePosition();
        Vec3 targetEyePos = this.currentTarget.getEyePosition();

        double dx = targetEyePos.x - playerEyePos.x;
        double dy = targetEyePos.y - playerEyePos.y;
        double dz = targetEyePos.z - playerEyePos.z;

        double horizontalDist = Math.sqrt(dx * dx + dz * dz);

        float targetYaw = (float) (Mth.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0F;
        float targetPitch = (float) (-(Mth.atan2(dy, horizontalDist) * (180.0 / Math.PI)));

        float lerpFactor = 1.0F - (float) Math.exp(-ROTATION_SPEED * deltaSeconds);

        float currentYaw = player.getYRot();
        float currentPitch = player.getXRot();

        float yawDelta = Mth.wrapDegrees(targetYaw - currentYaw);
        float pitchDelta = targetPitch - currentPitch;

        player.yRotO = currentYaw;
        player.xRotO = currentPitch;
        player.setYRot(currentYaw + yawDelta * lerpFactor);
        player.setXRot(currentPitch + pitchDelta * lerpFactor);

        if (this.lookTimer < this.lookAtPlayerDuration) {
            this.lookTimer += deltaSeconds;

            if (this.currentTarget instanceof LivingEntity livingTarget) {
                double revDx = playerEyePos.x - targetEyePos.x;
                double revDy = playerEyePos.y - targetEyePos.y;
                double revDz = playerEyePos.z - targetEyePos.z;
                double revHoriz = Math.sqrt(revDx * revDx + revDz * revDz);

                float lookYaw = (float) (Mth.atan2(revDz, revDx) * (180.0 / Math.PI)) - 90.0F;
                float lookPitch = (float) (-(Mth.atan2(revDy, revHoriz) * (180.0 / Math.PI)));

                livingTarget.xRotO = livingTarget.getXRot();
                livingTarget.yHeadRotO = livingTarget.getYHeadRot();

                float targetHeadYawDelta = Mth.wrapDegrees(lookYaw - livingTarget.getYHeadRot());
                float targetPitchDelta = lookPitch - livingTarget.getXRot();

                livingTarget.setXRot(livingTarget.getXRot() + targetPitchDelta * lerpFactor);
                livingTarget.setYHeadRot(livingTarget.getYHeadRot() + targetHeadYawDelta * lerpFactor);

                livingTarget.yBodyRot = this.lockedBodyYaw;
                livingTarget.yBodyRotO = this.lockedBodyYaw;
            }
        }
    }

    public void closeInteraction() {
        if (this.interacting) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.screen instanceof InteractionMiniScreen) {
                mc.setScreen(null);
            }
            this.resetState();
        }
    }

    public void freezeEntity() {
        EpicAPINetworkManager.sendToServer(new CPFreezeEntity(this.currentTarget.getId(), true));
    }

    public void unfreezeEntity() {
        EpicAPINetworkManager.sendToServer(new CPFreezeEntity(this.currentTarget.getId(), false));
    }

    public void resetState() {
        this.unfreezeEntity();
        this.interacting = false;
        this.currentTarget = null;
        this.lookTimer = 0.0F;
        this.lockedX = 0;
        this.lockedY = 0;
        this.lockedZ = 0;
        this.lockedYaw = 0;
        this.lockedPitch = 0;
        this.lockedBodyYaw = 0;
        this.interactionMiniScreen = null;
        this.pendingDialogueBuilder = null;
        this.clearPendingColorBuffers();
    }

    public void addFilter(Predicate<Entity> filter) {
        this.interactionFilters.add(filter);
    }

    public boolean canInteractWith(Entity entity) {
        if (entity == null) return false;

        for (Predicate<Entity> filter : this.interactionFilters) {
            if (!filter.test(entity)) {
                return false;
            }
        }
        return true;
    }

    public boolean isInteracting() {
        return this.interacting;
    }

    public boolean isInteractingWith(Entity entity) {
        return this.isInteracting() && this.currentTarget == entity;
    }

    public Entity getCurrentTarget() {
        return this.currentTarget;
    }

    public double getMaxInteractionDistanceSqr() {
        return this.maxInteractionDistanceSqr;
    }

    public void setMaxInteractionDistance(float blocks) {
        this.maxInteractionDistanceSqr = blocks * blocks;
    }

    public float getLookAtPlayerDuration() {
        return this.lookAtPlayerDuration;
    }

    public void setLookAtPlayerDuration(float seconds) {
        this.lookAtPlayerDuration = seconds;
    }

    public float getLockedYaw() {
        return this.lockedYaw;
    }

    public void setLockedYaw(float lockedYaw) {
        this.lockedYaw = lockedYaw;
    }

    public float getLockedPitch() {
        return this.lockedPitch;
    }

    public void setLockedPitch(float lockedPitch) {
        this.lockedPitch = lockedPitch;
    }
}