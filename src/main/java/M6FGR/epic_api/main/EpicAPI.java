package M6FGR.epic_api.main;

import M6FGR.epic_api.builders.minecraft.GameRulesBuilder;
import M6FGR.epic_api.cls.ILoadableClass;
import M6FGR.epic_api.events.IEventHook;
import M6FGR.epic_api.events.entity.EntityPatchEventHook;
import M6FGR.epic_api.events.item.ExCapCapabilityRegistryEventHook;
import M6FGR.epic_api.gameassets.EpicAPIKeyMappings;
import M6FGR.epic_api.gameassets.EpicAPISkillDataKeys;
import M6FGR.epic_api.gameassets.EpicAPISkills;
import M6FGR.epic_api.input.EpicAPIIntputAction;
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
        // never load events via ILoadableClass!, it will only fire for Epic-API!
        modBus.addListener(this::commonEvents);
        ILoadableClass.loadClasses(modBus,
                // Minecraft Builders
                GameRulesBuilder.class,
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
       event.enqueueWork(() -> {
           IEventHook.fire(
                   ExCapCapabilityRegistryEventHook.class,
                   EntityPatchEventHook.class
           );
       });
    }


}
