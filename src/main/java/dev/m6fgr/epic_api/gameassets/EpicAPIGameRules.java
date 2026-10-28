package dev.m6fgr.epic_api.gameassets;

import dev.m6fgr.epic_api.builders.minecraft.GameRulesBuilder;
import dev.m6fgr.epic_api.builders.minecraft.GameRulesBuilder.SynchronizedGameRule;
import net.minecraft.world.level.GameRules.BooleanValue;
import net.minecraft.world.level.GameRules.Category;
import org.jetbrains.annotations.ApiStatus.Experimental;

@Experimental
public class EpicAPIGameRules {
    public static final SynchronizedGameRule<BooleanValue> BOOLEAN_GAMERULE = GameRulesBuilder.newBoolean("booleanVal", Category.PLAYER, false, true);
}
