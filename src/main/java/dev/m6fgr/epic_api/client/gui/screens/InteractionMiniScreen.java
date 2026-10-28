package dev.m6fgr.epic_api.client.gui.screens;

import dev.m6fgr.epic_api.client.gui.InteractionAPI;
import dev.m6fgr.epic_api.client.gui.data.DialogueData;
import dev.m6fgr.epic_api.client.gui.data.OptionStyle;
import dev.m6fgr.epic_api.input.InputKeyCodes;
import dev.m6fgr.epic_api.network.EpicAPINetworkManager;
import dev.m6fgr.epic_api.network.packets.client.CPEntityInteraction;
import dev.m6fgr.epic_api.utils.color.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class InteractionMiniScreen extends Screen {

    private final Entity targetEntity;
    private final DialogueData dialogueData;
    private final OptionStyle optionStyle;
    private final List<Button> optionButtons = new ArrayList<>();
    private Button nextButton;

    private int visibleCharCount = 0;
    private boolean isTypingComplete = false;
    private int pauseTicksRemaining = 0;

    private static final int SHORT_PAUSE_TICKS = 4;
    private static final int LONG_PAUSE_TICKS = 10;

    private static final int BOX_WIDTH = 220;
    private static final int BOX_HEIGHT = 60;

    private int widgetColor = 0x80101010;
    private int widgetOutlineColor = 0xAA555555;
    private int entityNameColor = 0xFFFF55;
    private int textColor = 0xFFFFFF;

    public InteractionMiniScreen(Entity targetEntity, DialogueData dialogueData, OptionStyle optionStyle) {
        super(dialogueData.getSpeakerName());
        this.targetEntity = targetEntity;
        this.dialogueData = dialogueData;
        this.optionStyle = optionStyle;
    }

    public InteractionMiniScreen(Entity targetEntity, DialogueData dialogueData) {
        this(targetEntity, dialogueData, OptionStyle.CUSTOM);
    }

    @Override
    protected void init() {
        this.optionButtons.clear();

        int centerX = this.width / 2;
        int boxY = this.height - 100;
        int boxLeft = centerX - (BOX_WIDTH / 2);
        int buttonStartY = boxY + 38;

        int nextBtnWidth = 80;
        int margin = 4;

        this.nextButton = Button.builder(Component.literal(">"), b -> this.advanceToNextPage())
                .bounds(this.width - nextBtnWidth - margin, buttonStartY, nextBtnWidth, 18)
                .build();
        this.nextButton.visible = false;
        this.addRenderableWidget(this.nextButton);

        List<DialogueData.DialogueOption> options = this.dialogueData.getOptions();
        if (options.isEmpty()) return;

        switch (this.optionStyle) {
            case SINGLE -> {
                DialogueData.DialogueOption singleOption = options.getFirst();
                int btnWidth = 100;
                int btnX = centerX - (btnWidth / 2);

                Button btn = createOptionButton(singleOption, btnX, buttonStartY, btnWidth, 18);
                this.optionButtons.add(btn);
                this.addRenderableWidget(btn);
            }
            case QUESTION, CUSTOM -> {
                int totalOptions = options.size();
                int padding = 5;
                int totalPadding = padding * (totalOptions + 1);
                int availableWidth = BOX_WIDTH - totalPadding;
                int btnWidth = Math.max(30, availableWidth / totalOptions);

                for (int i = 0; i < totalOptions; i++) {
                    DialogueData.DialogueOption option = options.get(i);
                    int btnX = boxLeft + padding + i * (btnWidth + padding);

                    Button btn = createOptionButton(option, btnX, buttonStartY, btnWidth, 18);
                    this.optionButtons.add(btn);
                    this.addRenderableWidget(btn);
                }
            }
        }
    }

    private Button createOptionButton(DialogueData.DialogueOption option, int x, int y, int width, int height) {
        Button btn = Button.builder(option.label(), button -> {
            option.clientCallback().run();

            if (option.interactionType() != null && this.targetEntity != null) {
                EpicAPINetworkManager.sendToServer(new CPEntityInteraction(this.targetEntity.getId(), option.interactionType()));
            }
            this.onClose();
        }).bounds(x, y, width, height).build();

        btn.visible = false;
        return btn;
    }

    private void advanceToNextPage() {
        if (this.dialogueData.nextPage()) {
            this.visibleCharCount = 0;
            this.isTypingComplete = false;
            this.pauseTicksRemaining = 0;
            this.nextButton.visible = false;
        }
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // empty, we don't render it
    }

    @Override
    public void tick() {
        super.tick();
        String fullText = this.dialogueData.getCurrentText();

        if (this.isTypingComplete) return;

        if (this.pauseTicksRemaining > 0) {
            this.pauseTicksRemaining--;
            return;
        }

        int previousCharCount = this.visibleCharCount;
        this.visibleCharCount = Math.min(fullText.length(), this.visibleCharCount + this.dialogueData.getCharsPerTick());

        if (this.visibleCharCount < fullText.length()) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK.value(), 1.8F, 0.25F));

            for (int i = previousCharCount; i < this.visibleCharCount; i++) {
                char currentChar = fullText.charAt(i);
                int delay = this.getPunctuationPause(currentChar);

                if (delay > 0) {
                    this.pauseTicksRemaining = delay;
                    break;
                }
            }
        } else {
            this.finishSentence();
        }
    }

    private int getPunctuationPause(char c) {
        return switch (c) {
            case '.', '!', '?' -> LONG_PAUSE_TICKS;
            case ',', ';', ':' -> SHORT_PAUSE_TICKS;
            default -> 0;
        };
    }

    private void finishSentence() {
        this.isTypingComplete = true;
        this.pauseTicksRemaining = 0;
        this.visibleCharCount = this.dialogueData.getCurrentText().length();

        if (this.dialogueData.hasNextPage()) {
            this.nextButton.visible = true;
        } else {
            for (Button btn : this.optionButtons) {
                btn.visible = true;
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.isTypingComplete && button == 0) {
            this.finishSentence();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!this.isTypingComplete && (keyCode == InputKeyCodes.KEY_SPACE || keyCode == InputKeyCodes.KEY_NUMPADENTER)) {
            this.finishSentence();
            return true;
        }

        if (this.isTypingComplete) {
            if (this.dialogueData.hasNextPage() && (keyCode == InputKeyCodes.KEY_SPACE || keyCode == InputKeyCodes.KEY_NUMPADENTER)) {
                this.advanceToNextPage();
                return true;
            }

            if (!this.dialogueData.hasNextPage() && keyCode >= InputKeyCodes.KEY_1 && keyCode <= InputKeyCodes.KEY_9) {
                int optionIndex = keyCode - InputKeyCodes.KEY_1;
                if (optionIndex < this.optionButtons.size()) {
                    Button btn = this.optionButtons.get(optionIndex);
                    if (btn.visible && btn.active) {
                        btn.onPress();
                        return true;
                    }
                }
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int centerX = this.width / 2;
        int boxY = this.height - 100;
        int boxLeft = centerX - (BOX_WIDTH / 2);
        int boxRight = centerX + (BOX_WIDTH / 2);

        guiGraphics.fill(boxLeft, boxY, boxRight, boxY + BOX_HEIGHT, this.widgetColor);
        guiGraphics.renderOutline(boxLeft, boxY, BOX_WIDTH, BOX_HEIGHT, this.widgetOutlineColor);

        String pageHeader = this.dialogueData.getTotalPages() > 1
                ? " (" + (this.dialogueData.getCurrentPageIndex() + 1) + "/" + this.dialogueData.getTotalPages() + ")"
                : "";
        Component titleComponent = Component.literal(this.title.getString() + pageHeader);

        guiGraphics.drawString(this.font, titleComponent, boxLeft + 6, boxY + 6, this.entityNameColor, true);

        String fullText = this.dialogueData.getCurrentText();
        int displayLength = Math.min(this.visibleCharCount, fullText.length());
        String currentText = fullText.substring(0, displayLength);
        guiGraphics.drawWordWrap(this.font, Component.literal(currentText), boxLeft + 6, boxY + 18, BOX_WIDTH - 12, this.textColor);
    }

    public void setEntityNameColor(int entityNameColor) {
        this.entityNameColor = entityNameColor;
    }

    public void setEntityNameColor(Color entityNameColor) {
        this.setEntityNameColor(entityNameColor.getARGB());
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public void setTextColor(Color textColor) {
        this.setTextColor(textColor.getARGB());
    }

    public void setWidgetColor(int widgetColor) {
        this.widgetColor = widgetColor;
    }

    public void setWidgetColor(Color widgetColor) {
        this.setWidgetColor(widgetColor.getARGB());
    }

    public void setWidgetOutlineColor(int widgetOutlineColor) {
        this.widgetOutlineColor = widgetOutlineColor;
    }

    public void setWidgetOutlineColor(Color widgetOutlineColor) {
        this.setWidgetOutlineColor(widgetOutlineColor.getARGB());
    }

    @Override
    public void onClose() {
        super.onClose();
        InteractionAPI.getInstance().resetState();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}