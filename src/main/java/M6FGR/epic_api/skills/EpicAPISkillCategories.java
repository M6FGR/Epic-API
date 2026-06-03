package M6FGR.epic_api.skills;

import M6FGR.epic_api.gameassets.EpicAPIComponents;
import net.minecraft.network.chat.Component;
import yesman.epicfight.skill.SkillCategory;

public enum EpicAPISkillCategories implements SkillCategory {
    HEAVY_ATTACK(false, false, false, EpicAPIComponents.SKILL_CATEGORY_HEAVY_ATTACK),
    COUNTER_ATTACK(false, false, false, EpicAPIComponents.SKILL_CATEGORY_COUNTER_ATTACK);

    private final boolean shouldSave;
    private final boolean synchronize;
    private final boolean learnable;
    private final Component translationKey;
    private final int id;

    EpicAPISkillCategories(boolean shouldSave, boolean synchronize, boolean learnable, Component translationKey) {
        this.shouldSave = shouldSave;
        this.synchronize = synchronize;
        this.learnable = learnable;
        this.translationKey = translationKey;
        this.id = SkillCategory.ENUM_MANAGER.assign(this);
    }

    @Override
    public boolean shouldSave() {
        return this.shouldSave;
    }

    @Override
    public boolean shouldSynchronize() {
        return this.synchronize;
    }

    @Override
    public boolean learnable() {
        return this.learnable;
    }

    @Override
    public Component getTranslationKey() {
        return this.translationKey;
    }

    @Override
    public int universalOrdinal() {
        return this.id;
    }
}
