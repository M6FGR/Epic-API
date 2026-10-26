package dev.m6fgr.epic_api.client.gui.data;

import dev.m6fgr.epic_api.client.gui.InteractionType;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class DialogueData {

    private final Component speakerName;
    private final List<String> pages;
    private int currentPageIndex = 0;
    private final int charsPerTick;
    private final OptionStyle optionStyle;
    private final List<DialogueOption> options = new ArrayList<>();

    public DialogueData(Component speakerName, List<String> pages, int charsPerTick, OptionStyle optionStyle) {
        this.speakerName = speakerName;
        this.pages = pages.isEmpty() ? List.of("...") : pages;
        this.charsPerTick = charsPerTick;
        this.optionStyle = optionStyle;
    }

    public static Builder builder(Component speakerName, String text) {
        return new Builder(speakerName, text);
    }

    public static Builder builder(String speakerName, String text) {
        return builder(Component.literal(speakerName), text);
    }

    public Component getSpeakerName() {
        return this.speakerName;
    }

    public String getCurrentText() {
        return this.pages.get(this.currentPageIndex);
    }

    public boolean hasNextPage() {
        return this.currentPageIndex < this.pages.size() - 1;
    }

    public boolean nextPage() {
       if (this.hasNextPage()) {
           this.currentPageIndex++;
           return true;
       }
       return false;
    }

    public int getCurrentPageIndex() {
        return this.currentPageIndex;
    }

    public int getTotalPages() {
        return this.pages.size();
    }

    public int getCharsPerTick() {
        return this.charsPerTick;
    }

    public OptionStyle getOptionStyle() {
        return this.optionStyle;
    }

    public List<DialogueOption> getOptions() {
        return this.options;
    }

    public record DialogueOption(
            Component label,
            InteractionType interactionType,
            Runnable clientCallback
    ) {}

    public static class Builder {
        private Component speakerName;
        private final List<String> pages = new ArrayList<>();
        private int charsPerTick = 2;
        private OptionStyle optionStyle = OptionStyle.CUSTOM;
        private final List<DialogueOption> options = new ArrayList<>();

        public Builder(Component speakerName, String text) {
            this.speakerName = speakerName;
            if (text != null && !text.isEmpty()) {
                this.pages.add(text);
            }
        }

        public Builder speakerName(Component speakerName) {
            this.speakerName = speakerName;
            return this;
        }

        public Builder speakerName(String speakerName) {
            this.speakerName = Component.literal(speakerName);
            return this;
        }

        public Builder text(String text) {
            this.pages.clear();
            this.pages.add(text);
            return this;
        }

        public Builder addPage(String pageText) {
            this.pages.add(pageText);
            return this;
        }

        public Builder speed(int charsPerTick) {
            this.charsPerTick = Math.max(1, charsPerTick);
            return this;
        }

        public Builder charsPerTick(int charsPerTick) {
            return this.speed(charsPerTick);
        }

        public Builder style(OptionStyle style) {
            this.optionStyle = style;
            return this;
        }

        public Builder clearOptions() {
            this.options.clear();
            return this;
        }

        public Builder addOption(Component label, InteractionType interactionType, Runnable clientCallback) {
            this.options.add(new DialogueOption(label, interactionType, clientCallback != null ? clientCallback : () -> {}));
            return this;
        }

        public Builder addOption(String labelText, InteractionType interactionType, Runnable clientCallback) {
            return addOption(Component.literal(labelText), interactionType, clientCallback);
        }

        public Builder addOption(Component label, InteractionType interactionType) {
            return addOption(label, interactionType, () -> {});
        }

        public Builder addOption(String labelText, InteractionType interactionType) {
            return addOption(Component.literal(labelText), interactionType, () -> {});
        }

        public Builder addClientOption(Component label, Runnable clientCallback) {
            return addOption(label, null, clientCallback);
        }

        public Builder addClientOption(String labelText, Runnable clientCallback) {
            return addOption(Component.literal(labelText), null, clientCallback);
        }

        public DialogueData build() {
            DialogueData data = new DialogueData(this.speakerName, this.pages, this.charsPerTick, this.optionStyle);
            data.options.addAll(this.options);
            return data;
        }
    }
}