package M6FGR.epic_api.main;

import M6FGR.epic_api.api.builders.epicfight.ArmatureRegistrar;
import M6FGR.epic_api.api.builders.epicfight.EntityPatchRegistrar;
import M6FGR.epic_api.api.builders.minecraft.GameRulesRegistrar;
import M6FGR.epic_api.api.cls.load.ILoadableClass;
import M6FGR.epic_api.api.events.IEventHook;
import M6FGR.epic_api.api.events.item.ExCapCapabilityRegistryEventHook;
import M6FGR.epic_api.api.input.EpicAPIIntputAction;
import M6FGR.epic_api.gameassets.EpicAPIKeyMappings;
import M6FGR.epic_api.gameassets.EpicAPISkillDataKeys;
import M6FGR.epic_api.gameassets.EpicAPISkills;
import M6FGR.epic_api.skills.EpicAPISkillCategories;
import M6FGR.epic_api.skills.EpicAPISkillSlots;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yesman.epicfight.api.client.input.action.InputAction;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SkillSlot;

@Mod(EpicAPI.MODID)
public class EpicAPI {
    public static final String MODID = "epic_api";
    public static final Logger LOGGER = LogManager.getLogger("EpicAPI");

    public EpicAPI(IEventBus modBus) {
        // never load events via ILoadableClass!, it will only fire for me
        modBus.addListener(this::commonEvents);

        ILoadableClass.loadClasses(modBus,
                // EpicFight registrars
                ArmatureRegistrar.class,
                EntityPatchRegistrar.class,
                // Minecraft Registrars
                GameRulesRegistrar.class,
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

    public static boolean isDeveloper() {
        return !FMLEnvironment.production;
    }

    private void commonEvents(FMLCommonSetupEvent event) {
        IEventHook.fire(
                ExCapCapabilityRegistryEventHook.class
        );
    }


}
