package dev.m6fgr.epic_api.events.epic_api.entity;

import dev.m6fgr.epic_api.client.gui.InteractionAPI;
import dev.m6fgr.epic_api.client.gui.InteractionType;
import dev.m6fgr.epic_api.client.gui.data.OptionStyle;
import dev.m6fgr.epic_api.events.dispatch.IDispatchableEvent;
import dev.m6fgr.epic_api.events.dispatch.abstracts.DispatchableEvent;
import dev.m6fgr.epic_api.events.dispatch.marks.HybridEvent;
import dev.m6fgr.epic_api.events.epic_api.EpicAPIDispatchableEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

@HybridEvent
public class EntityInteractDispatchableEvent extends DispatchableEvent implements IDispatchableEvent<EntityInteractDispatchableEvent> {

    private final Player interactor;
    private final Entity interactedTarget;
    private final InteractionAPI interactionAPI;

    public EntityInteractDispatchableEvent(Player interactor, Entity interactedTarget, InteractionAPI interactionAPI) {
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

    public EntityInteractDispatchableEvent setSpeakerName(Component name) {
        this.interactionAPI.setSpeakerName(name);
        return this;
    }

    public EntityInteractDispatchableEvent addPage(String pageText, OptionStyle style) {
        this.interactionAPI.addPage(pageText);
        this.setOptionStyle(style);
        return this;
    }

    public EntityInteractDispatchableEvent addPage(String pageText) {
        this.interactionAPI.addPage(pageText);
        return this;
    }

    public EntityInteractDispatchableEvent setSpeakerName(String name) {
        this.interactionAPI.setSpeakerName(Component.literal(name));
        return this;
    }

    public EntityInteractDispatchableEvent setDialogueText(String text) {
        this.interactionAPI.setDialogueText(text);
        return this;
    }

    public EntityInteractDispatchableEvent setCharsPerTick(int charsPerTick) {
        this.interactionAPI.setCharsPerTick(charsPerTick);
        return this;
    }

    public EntityInteractDispatchableEvent setOptionStyle(OptionStyle optionStyle) {
        this.interactionAPI.setOptionStyle(optionStyle);
        return this;
    }

    public EntityInteractDispatchableEvent addOption(Component label, InteractionType interactionType, Runnable clientCallback) {
        this.interactionAPI.addOption(label, interactionType, clientCallback);
        return this;
    }

    public EntityInteractDispatchableEvent addOption(String label, InteractionType interactionType, Runnable clientCallback) {
        return this.addOption(Component.literal(label), interactionType, clientCallback);
    }

    public EntityInteractDispatchableEvent addOption(String label, Runnable clientCallback) {
        return this.addOption(Component.literal(label), null, clientCallback);
    }

    public EntityInteractDispatchableEvent addOption(String label, InteractionType interactionType) {
        return this.addOption(Component.literal(label), interactionType, () -> {});
    }

    @Override
    public EntityInteractDispatchableEvent post() {
       return EpicAPIDispatchableEvents.Entity.ENTITY_INTERACT.postEvent(this);
    }
}
