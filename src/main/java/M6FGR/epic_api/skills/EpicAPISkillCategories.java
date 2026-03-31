package M6FGR.epic_api.skills;

import net.minecraft.network.chat.Component;
import yesman.epicfight.skill.SkillCategory;

public enum EpicAPISkillCategories implements SkillCategory {
    HEAVY_ATTACK(false, false, false, "skill.epic_api.category.heavy_attack");

    final boolean shouldSave;
    final boolean synchronize;
    final boolean learnable;
    final Component translationKey;
    final int id;

    EpicAPISkillCategories(boolean shouldSave, boolean synchronize, boolean learnable, String translationKey) {
        this.shouldSave = shouldSave;
        this.synchronize = synchronize;
        this.learnable = learnable;
        this.translationKey = Component.translatable(translationKey);
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
