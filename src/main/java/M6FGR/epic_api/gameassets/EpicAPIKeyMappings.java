package M6FGR.epic_api.gameassets;

import M6FGR.epic_api.builders.minecraft.KeyMappingsBuilder;
import M6FGR.epic_api.builders.minecraft.KeyMappingsBuilder.InputType;
import M6FGR.epic_api.builders.minecraft.KeyMappingsBuilder.KeyCategory;
import M6FGR.epic_api.cls.ILoadableClass;
import M6FGR.epic_api.input.KeyCodes;
import M6FGR.epic_api.main.EpicAPI;
import net.minecraft.client.KeyMapping;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

import java.util.ArrayList;
import java.util.List;

public class EpicAPIKeyMappings implements ILoadableClass {
    private static final List<KeyMapping> KEY_MAPPINGS = new ArrayList<>();
    public static KeyMapping HEAVY_ATTACK = KeyMappingsBuilder.newCombatKeyMapping(
            EpicAPIComponents.HEAVY_ATTACK,
            InputType.MOUSE,
            KeyCodes.KEY_X,
            KeyCategory.EPICFIGHT_COMBAT
    );

    static {
        KEY_MAPPINGS.add(HEAVY_ATTACK);
    }

    @Override
    public void onModClientConstructor(IEventBus modBus) {
        modBus.addListener(this::onKeysRegistry);
        EpicAPI.debug("Registered EpicAPI Keymappings.");
    }



    private void onKeysRegistry(RegisterKeyMappingsEvent event) {
        for (KeyMapping keyMapping : KEY_MAPPINGS) {
            event.register(keyMapping);
        }
    }


}
