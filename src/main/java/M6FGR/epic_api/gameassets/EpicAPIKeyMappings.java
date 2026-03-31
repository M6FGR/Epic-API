package M6FGR.epic_api.gameassets;

import M6FGR.epic_api.api.cls.ILoadableClass;
import M6FGR.epic_api.main.EpicAPI;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import yesman.epicfight.client.input.CombatKeyMapping;

public class EpicAPIKeyMappings implements ILoadableClass {
    public static final KeyMapping HEAVY_ATTACK;

    private void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(HEAVY_ATTACK);
        EpicAPI.LOGGER.info("Registered all key mappings!");
    }
    static {
        HEAVY_ATTACK = new CombatKeyMapping("key.epic_api.heavy_attack", InputConstants.Type.KEYSYM, 82, "key.epicfight.combat");
    }

    // can be passed even as a subscribe event
    @Override
    public void onModConstructor(IEventBus bus) {
        bus.addListener(this::registerKeys);
    }
}
