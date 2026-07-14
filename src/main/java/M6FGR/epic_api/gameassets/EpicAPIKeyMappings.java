package M6FGR.epic_api.gameassets;

import M6FGR.epic_api.builders.minecraft.client.KeyMappingsBuilder;
import M6FGR.epic_api.builders.minecraft.client.KeyMappingsBuilder.InputType;
import M6FGR.epic_api.builders.minecraft.client.KeyMappingsBuilder.KeyCategory;
import M6FGR.epic_api.cls.ILoadableClass;
import M6FGR.epic_api.input.KeyCodes;
import M6FGR.epic_api.main.EpicAPI;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import yesman.epicfight.api.utils.side.ClientOnly;
import yesman.epicfight.client.input.CombatKeyMapping;

import java.util.ArrayList;
import java.util.List;

@ClientOnly
public class EpicAPIKeyMappings implements ILoadableClass {
    private static final List<KeyMapping> KEY_MAPPINGS = new ArrayList<>();

    public static CombatKeyMapping HEAVY_ATTACK;

    public static CombatKeyMapping COUNTER_ATTACK;

    private static KeyMapping registerKey(Component name, InputType inputType, int keyCode, KeyCategory category) {
        KeyMapping keyMapping = KeyMappingsBuilder.newKeyMapping(name.getString(), keyCode, inputType, category);
        if (KEY_MAPPINGS.contains(keyMapping)) {
            EpicAPI.errIfDevSide("Keymapping: [{}] is already registered!", name.getString());
            return null;
        }
        KEY_MAPPINGS.add(keyMapping);
        return keyMapping;
    }

    private static CombatKeyMapping registerCombatKey(Component name, InputType inputType, int keyCode) {
        CombatKeyMapping keyMapping = KeyMappingsBuilder.newCombatKeyMapping(name.getString(), inputType, keyCode, KeyCategory.EPICFIGHT_COMBAT);
        if (KEY_MAPPINGS.contains(keyMapping)) {
            EpicAPI.errIfDevSide("Keymapping: [{}] is already registered!", name.getString());
            return null;
        }
        KEY_MAPPINGS.add(keyMapping);
        return keyMapping;
    }

    @Override
    public void onModClientConstructor(IEventBus modBus) {
        COUNTER_ATTACK = registerCombatKey(
                EpicAPIComponents.KEY_COUNTER_ATTACK,
                InputType.MOUSE,
                KeyCodes.MOUSE_LEFT_CLICK
        );

        HEAVY_ATTACK = registerCombatKey(
                EpicAPIComponents.KEY_HEAVY_ATTACK,
                InputType.KEYBOARD,
                KeyCodes.KEY_X
        );
        modBus.addListener(this::onKeysRegistry);
    }

    private void onKeysRegistry(RegisterKeyMappingsEvent event) {
        for (KeyMapping keyMapping : KEY_MAPPINGS) {
            event.register(keyMapping);
        }
    }

}