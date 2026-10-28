package dev.m6fgr.epic_api.events.epic_api.entity;

import dev.m6fgr.epic_api.client.gui.InteractionAPI;
import dev.m6fgr.epic_api.client.gui.InteractionType;
import dev.m6fgr.epic_api.client.gui.data.OptionStyle;
import dev.m6fgr.epic_api.events.dispatch.IDispatchableEvent;
import dev.m6fgr.epic_api.events.dispatch.abstracts.DispatchEvent;
import dev.m6fgr.epic_api.events.dispatch.marks.HybridEvent;
import dev.m6fgr.epic_api.events.epic_api.EpicAPIDispatchableEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

@HybridEvent
public class EntityInteractDispatchEvent extends DispatchEvent implements IDispatchableEvent<EntityInteractDispatchEvent> {

    private final Player interactor;
    private final Entity interactedTarget;
    private final InteractionAPI interactionAPI;

    public EntityInteractDispatchEvent(Player interactor, Entity interactedTarget, InteractionAPI interactionAPI) {
        this.interactor = interactor;
        this.interactedTarget = interactedTarget;
        this.interactionAPI = interactionAPI;
    }

    public Player getInteractor() {
        return this.interactor;
    }

    public Entity getSpeaker() {
        return this.interactedTarget;
    }

    public boolean isSpeakerInstanceOf(Class<? extends Entity> entityClass) {
        if (this.interactedTarget == null || entityClass == null) {
            return false;
        }
        return entityClass.isInstance(this.interactedTarget);
    }

    public InteractionAPI getInteractionAPI() {
        return this.interactionAPI;
    }

    public EntityInteractDispatchEvent setSpeakerName(Component name) {
        this.interactionAPI.setSpeakerName(name);
        return this;
    }

    public EntityInteractDispatchEvent addPage(String pageText, OptionStyle style) {
        this.interactionAPI.addPage(pageText);
        this.setOptionStyle(style);
        return this;
    }

    public EntityInteractDispatchEvent addPage(String pageText) {
        this.interactionAPI.addPage(pageText);
        return this;
    }

    public EntityInteractDispatchEvent setSpeakerName(String name) {
        this.interactionAPI.setSpeakerName(Component.literal(name));
        return this;
    }

    public EntityInteractDispatchEvent setDialogueText(String text) {
        this.interactionAPI.setDialogueText(text);
        return this;
    }

    public EntityInteractDispatchEvent setCharsPerTick(int charsPerTick) {
        this.interactionAPI.setCharsPerTick(charsPerTick);
        return this;
    }

    public EntityInteractDispatchEvent setOptionStyle(OptionStyle optionStyle) {
        this.interactionAPI.setOptionStyle(optionStyle);
        return this;
    }

    public EntityInteractDispatchEvent addOption(Component label, InteractionType interactionType, Runnable clientCallback) {
        this.interactionAPI.addOption(label, interactionType, clientCallback);
        return this;
    }

    public EntityInteractDispatchEvent addOption(String label, InteractionType interactionType, Runnable clientCallback) {
        return this.addOption(Component.literal(label), interactionType, clientCallback);
    }

    public EntityInteractDispatchEvent addOption(String label, Runnable clientCallback) {
        return this.addOption(Component.literal(label), null, clientCallback);
    }

    public EntityInteractDispatchEvent addOption(String label, InteractionType interactionType) {
        return this.addOption(Component.literal(label), interactionType, () -> {});
    }

    @Override
    public EntityInteractDispatchEvent post() {
       return EpicAPIDispatchableEvents.Entity.ENTITY_INTERACT.postEvent(this);
    }
}
