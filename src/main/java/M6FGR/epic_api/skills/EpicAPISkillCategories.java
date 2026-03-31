package M6FGR.epic_api.skills;

import yesman.epicfight.skill.SkillCategory;

public enum EpicAPISkillCategories implements SkillCategory {
    HEAVY_ATTACK(false, false, false);

    final boolean shouldSave;
    final boolean synchronize;
    final boolean learnable;
    final int id;

    EpicAPISkillCategories(boolean shouldSave, boolean synchronize, boolean learnable){
        this.shouldSave = shouldSave;
        this.synchronize = synchronize;
        this.learnable = learnable;
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
    public int universalOrdinal() {
        return this.id;
    }
}
