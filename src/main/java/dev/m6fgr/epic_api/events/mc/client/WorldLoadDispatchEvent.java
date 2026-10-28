package dev.m6fgr.epic_api.events.mc.client;

import dev.m6fgr.epic_api.events.dispatch.IDispatchableEvent;
import dev.m6fgr.epic_api.events.dispatch.abstracts.DispatchEvent;
import dev.m6fgr.epic_api.events.mc.MinecraftDispatchableEvents;
import net.minecraft.server.WorldStem;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;

public abstract class WorldLoadDispatchEvent extends DispatchEvent implements IDispatchableEvent<WorldLoadDispatchEvent> {
    protected final LevelStorageSource.LevelStorageAccess levelStorageAccess;
    protected final PackRepository packRepository;
    protected final WorldStem stem;
    protected final boolean isNewWorld;

    public WorldLoadDispatchEvent(LevelStorageSource.LevelStorageAccess levelStorage, PackRepository packRepository, WorldStem worldStem, boolean newWorld) {
        this.levelStorageAccess = levelStorage;
        this.packRepository = packRepository;
        this.stem = worldStem;
        this.isNewWorld = newWorld;
    }

    public LevelStorageAccess getLevelStorageAccess() {
        return this.levelStorageAccess;
    }

    public PackRepository getPackRepository() {
        return this.packRepository;
    }

    public WorldStem getStem() {
        return this.stem;
    }

    public boolean isNewWorld() {
        return this.isNewWorld;
    }


    public abstract WorldLoadDispatchEvent post();


    // fires if the player is initializing a world
    public static class Initialize extends WorldLoadDispatchEvent {
        public Initialize(LevelStorageAccess levelStorage, PackRepository packRepository, WorldStem worldStem, boolean newWorld) {
            super(levelStorage, packRepository, worldStem, newWorld);
        }

        @Override
        public Initialize post() {
            return MinecraftDispatchableEvents.Client.WORLD_INIT.postEvent(this);
        }
    }

    // fires if the player joins an already existing world
    public static class Join extends WorldLoadDispatchEvent {
        public Join(LevelStorageAccess levelStorage, PackRepository packRepository, WorldStem worldStem) {
            super(levelStorage, packRepository, worldStem, false);
        }

        @Override
        public Join post() {
            return MinecraftDispatchableEvents.Client.WORLD_JOIN.postEvent(this);
        }
    }

    // fires if the player creates a new world
    public static class Create extends WorldLoadDispatchEvent {
        public Create(LevelStorageAccess levelStorage, PackRepository packRepository, WorldStem worldStem) {
            super(levelStorage, packRepository, worldStem, true);
        }

        @Override
        public Create post() {
            return MinecraftDispatchableEvents.Client.WORLD_CREATE.postEvent(this);
        }
    }
}
