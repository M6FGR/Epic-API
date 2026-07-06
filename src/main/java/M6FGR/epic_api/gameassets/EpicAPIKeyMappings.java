package M6FGR.epic_api.gameassets;

import M6FGR.epic_api.cls.ILoadableClass;
import M6FGR.epic_api.input.KeyCodes;
import M6FGR.epic_api.main.EpicAPI;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Type;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import yesman.epicfight.client.input.CombatKeyMapping;
import yesman.epicfight.generated.LangKeys;

import java.util.ArrayList;
import java.util.List;
public class EpicAPIKeyMappings implements ILoadableClass {

    private static final List<KeyMapping> KEY_MAPPINGS = new ArrayList<>();
    public static final KeyMapping HEAVY_ATTACK = newCombatKeyMapping(
            EpicAPIComponents.HEAVY_ATTACK_KEY,
            InputType.KEYBOARD,
            KeyCodes.KEY_X,
            LangKeys.KEY_COMBAT
    );

    public static final KeyMapping COUNTER_ATTACK = newCombatKeyMapping(
            EpicAPIComponents.COUNTER_ATTACK_KEY,
            InputType.MOUSE,
            KeyCodes.MOUSE_LEFT_CLICK,
            LangKeys.KEY_COMBAT
    );


    @Override
    public void onModClientConstructor(IEventBus modBus) {
        modBus.addListener(this::onKeysRegistry);
    }

    private void onKeysRegistry(RegisterKeyMappingsEvent event) {
        for (KeyMapping keyMapping : KEY_MAPPINGS) {
            event.register(keyMapping);
        }
        EpicAPI.debug("Registered EpicAPI Keymappings.");
    }

    // Helper methods
    public static KeyMapping newKeyMapping(
            Component component,
            InputType type,
            int keyCode,
            String category
    ) {
        KeyMapping key = new KeyMapping(component.getString(), type.getInputType(), keyCode, category);
        KEY_MAPPINGS.add(key);
        return key;
    }

    public static CombatKeyMapping newCombatKeyMapping(
            Component component,
            InputType type,
            int keyCode,
            String category
    ) {
        CombatKeyMapping combatKey = new CombatKeyMapping(component.getString(), type.getInputType(), keyCode, category);
        KEY_MAPPINGS.add(combatKey);
        return combatKey;
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
}
