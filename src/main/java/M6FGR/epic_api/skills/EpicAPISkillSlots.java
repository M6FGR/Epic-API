package M6FGR.epic_api.skills;

import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SkillSlot;

public enum EpicAPISkillSlots implements SkillSlot {
    HEAVY_ATTACK(EpicAPISkillCategories.HEAVY_ATTACK);

    final int id;
    final SkillCategory category;
    EpicAPISkillSlots(SkillCategory category) {
        this.id = SkillSlot.ENUM_MANAGER.assign(this);
        this.category = category;

    }
    @Override
    public SkillCategory category() {
        return this.category;
    }

    @Override
    public int universalOrdinal() {
        return this.id;
    }
}
