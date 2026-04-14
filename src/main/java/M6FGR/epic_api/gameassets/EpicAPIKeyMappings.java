package M6FGR.epic_api.gameassets;

import M6FGR.epic_api.cls.ILoadableClass;
import M6FGR.epic_api.main.EpicAPI;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.bus.api.IEventBus;
import yesman.epicfight.client.input.CombatKeyMapping;
import yesman.epicfight.platform.client.ClientModPlatformProvider;

public class EpicAPIKeyMappings implements ILoadableClass {
    public static final KeyMapping HEAVY_ATTACK = new CombatKeyMapping(
            "key.epic_api.heavy_attack",
            InputConstants.Type.KEYSYM,
            82,
            "key.epicfight.combat"
    );


    @Override
    public void onModClientConstructor(IEventBus modBus) {
        ClientModPlatformProvider.get().keyMappingRegistrar().registerKeyMapping(HEAVY_ATTACK);
        EpicAPI.LOGGER.info("registered keymappings");
    }
}
