package M6FGR.epic_api.builders.minecraft;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Type;
import net.minecraft.client.KeyMapping;
import org.jetbrains.annotations.ApiStatus.Experimental;
import yesman.epicfight.client.input.CombatKeyMapping;
@Experimental
public class KeyMappingsBuilder {

    public static KeyMapping newKeyMapping(String name, int keyCode, InputType inputType, KeyCategory category) {
        return new KeyMapping(name, inputType.getInputType(), keyCode, category.get());
    }

    public static CombatKeyMapping newCombatKeyMapping(String name, InputType inputType, int keyCode, KeyCategory category) {
      return new CombatKeyMapping(name, inputType.getInputType(), keyCode, category.get());
    }


    public enum InputType {
        KEYBOARD(Type.KEYSYM),
        SCANCODE(Type.SCANCODE),
        MOUSE(Type.MOUSE);

        private final Type inputType;
        InputType(InputConstants.Type type) {
            this.inputType = type;
        }

        public Type getInputType() {
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
