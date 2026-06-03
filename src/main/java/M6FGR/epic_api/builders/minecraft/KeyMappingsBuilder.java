package M6FGR.epic_api.builders.minecraft;

import M6FGR.epic_api.main.EpicAPI;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import yesman.epicfight.client.input.CombatKeyMapping;

public class KeyMappingsBuilder {

    private KeyMappingsBuilder() {}

    public static KeyMapping newKeyMapping(String name, int keyCode, InputType inputType, KeyCategory category) {
        if (EpicAPI.getEnvHelper().isDedicatedServer()) return null;
        return new KeyMapping(name, inputType.get(), keyCode, category.get());
    }

    public static CombatKeyMapping newCombatKeyMapping(String name, InputType inputType, int keyCode, KeyCategory category) {
        if (EpicAPI.getEnvHelper().isDedicatedServer()) return null;
        return new CombatKeyMapping(name, inputType.get(), keyCode, category.get());
    }

    public enum InputType {
        KEYBOARD(InputConstants.Type.KEYSYM),
        SCANCODE(InputConstants.Type.SCANCODE),
        MOUSE(InputConstants.Type.MOUSE);

        private final InputConstants.Type inputType;

        InputType(InputConstants.Type type) {
            this.inputType = type;
        }

        public InputConstants.Type get() {
            return this.inputType;
        }
    }

    public enum KeyCategory {
        // Vanilla Categories
        MOVEMENT("key.categories.movement"),
        MISC("key.categories.misc"),
        MULTIPLAYER("key.categories.multiplayer"),
        GAMEPLAY("key.categories.gameplay"),
        INVENTORY("key.categories.inventory"),
        UI("key.categories.ui"),
        CREATIVE("key.categories.creative"),
        COMBAT("key.categories.combat"),

        // EpicFight Categories
        EPICFIGHT_COMBAT("key.epicfight.combat");

        private String translationKey;
        KeyCategory(String translationKey) {
            this.translationKey = translationKey;
        }

        /**
         * @return The translation key used by Minecraft's KeyMapping system.
         */
        public String get() {
            return this.translationKey;
        }
        // in case you want to add your own
        public static KeyCategory of(String translationKey) {
            MISC.translationKey = translationKey;
            return MISC;
        }
    }
}
