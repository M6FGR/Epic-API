package dev.m6fgr.epic_api.gameassets;

import dev.m6fgr.epic_api.builders.minecraft.client.KeyMappingsBuilder;
import dev.m6fgr.epic_api.builders.minecraft.client.KeyMappingsBuilder.InputType;
import dev.m6fgr.epic_api.builders.minecraft.client.KeyMappingsBuilder.KeyCategory;
import dev.m6fgr.epic_api.cls.loadable.ILoadableClass;
import dev.m6fgr.epic_api.input.KeyCodes;
import dev.m6fgr.epic_api.main.EpicAPI;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import yesman.epicfight.client.input.CombatKeyMapping;

import java.util.ArrayList;
import java.util.List;


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
    public void onModClientConstruct(IEventBus modBus) {
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
        modBus.addListener(EpicAPIKeyMappings::onKeysRegistry);
    }

    private static void onKeysRegistry(RegisterKeyMappingsEvent event) {
        for (KeyMapping keyMapping : KEY_MAPPINGS) {
            event.register(keyMapping);
        }
    }

}