package dev.m6fgr.epic_api.cls.loadable.enums;

import dev.m6fgr.epic_api.client.sound.data.EpicAPISoundDataManager;
import dev.m6fgr.epic_api.cls.loadable.ILoadableClass;
import dev.m6fgr.epic_api.gameassets.EpicAPIKeyMappings;
import dev.m6fgr.epic_api.gameassets.EpicAPISkillDataKeys;
import dev.m6fgr.epic_api.gameassets.EpicAPISkills;
import dev.m6fgr.epic_api.network.EpicAPINetworkManager;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public enum EpicAPILoadableEnums implements LoadableEnum {
    NETWORK_MANAGER(EpicAPINetworkManager.class),
    SOUND_MANAGER(EpicAPISoundDataManager.class),
    SKILLS(EpicAPISkills.class),
    SKILL_DATA_KEYS(EpicAPISkillDataKeys.class),
    // Client Registries
    KEY_MAPPINGS(EpicAPIKeyMappings.class, true);

    private final Class<? extends ILoadableClass> loadableClass;
    private boolean clientOnly;

    EpicAPILoadableEnums(Class<? extends ILoadableClass> loadableClass) {
        this.loadableClass = loadableClass;
    }

    EpicAPILoadableEnums(Class<? extends ILoadableClass> loadableClass, boolean clientOnly) {
        this(loadableClass);
        this.clientOnly = clientOnly;
    }

    public Class<? extends ILoadableClass> getLoadableClass() {
        return this.loadableClass;
    }

    public boolean isClientOnly() {
        return this.clientOnly;
    }
}
