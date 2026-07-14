package M6FGR.epic_api.gameassets;

import net.minecraft.network.chat.Component;

// Stores translation keys, names, any kind of text-asset
public final class EpicAPIComponents {
    private EpicAPIComponents() {}

    public static final Component KEY_HEAVY_ATTACK = Component.translatable("key.epic_api.heavy_attack");
    public static final Component KEY_COUNTER_ATTACK = Component.translatable("key.epic_api.counter_attack");
    public static final Component SKILL_CATEGORY_HEAVY_ATTACK = Component.translatable("skill.epic_api.category.heavy_attack");
    public static final Component SKILL_CATEGORY_COUNTER_ATTACK = Component.translatable("skill.epic_api.category.counter_attack");

}
