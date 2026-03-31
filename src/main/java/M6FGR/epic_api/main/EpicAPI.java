package M6FGR.epic_api.main;

import M6FGR.epic_api.api.cls.ILoadableClass;
import M6FGR.epic_api.api.input.EpicAPIIntputAction;
import M6FGR.epic_api.api.registry.ArmatureRegistry;
import M6FGR.epic_api.api.registry.EntityPatchRegistry;
import M6FGR.epic_api.gameassets.EpicAPIKeyMappings;
import M6FGR.epic_api.gameassets.EpicAPISkillDataKeys;
import M6FGR.epic_api.gameassets.EpicAPISkills;
import M6FGR.epic_api.skills.EpicAPISkillCategories;
import M6FGR.epic_api.skills.EpicAPISkillSlots;
import M6FGR.epic_api.world.capabilities.item.WeaponCapabilityPresets;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yesman.epicfight.api.client.input.action.InputAction;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SkillSlot;

@Mod(EpicAPI.MODID)
public class EpicAPI {
    public static final String MODID = "epic_api";
    public static final Logger LOGGER = LogManager.getLogger();

    public EpicAPI(IEventBus modBus) {
        ILoadableClass.loadClasses(modBus,
                // API registry
                ArmatureRegistry.class,
                WeaponCapabilityPresets.class,
                EntityPatchRegistry.class,
                // Assets registry
                EpicAPISkills.class,
                EpicAPISkillDataKeys.class,
                EpicAPIKeyMappings.class
        );
        // EpicFight Extensible Enums Registry
        SkillSlot.ENUM_MANAGER.registerEnumCls(MODID, EpicAPISkillSlots.class);
        SkillCategory.ENUM_MANAGER.registerEnumCls(MODID, EpicAPISkillCategories.class);
        InputAction.ENUM_MANAGER.registerEnumCls(MODID, EpicAPIIntputAction.class);
    }

}
