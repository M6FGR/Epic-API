package M6FGR.epic_api.gameassets;

import M6FGR.epic_api.builders.minecraft.GameRulesBuilder;
import M6FGR.epic_api.cls.ILoadableClass;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameRules.BooleanValue;
import net.minecraft.world.level.GameRules.Category;
import net.minecraftforge.eventbus.api.IEventBus;
import yesman.epicfight.world.gamerule.EpicFightGameRules;

public class EpicAPIGameRules implements ILoadableClass {
    public static final GameRules.Key<BooleanValue> SWITCHABLE_CAMERA = GameRulesBuilder.newBoolean("switchableCamera", Category.PLAYER, false, true);

    @Override
    public void onModConstructor(IEventBus modBus) {
        SWITCHABLE_CAMERA.toString();
    }
}
