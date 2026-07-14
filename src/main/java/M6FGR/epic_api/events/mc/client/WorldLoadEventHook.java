package M6FGR.epic_api.events.mc.client;

import M6FGR.epic_api.events.IEventHook;
import M6FGR.epic_api.events.mc.MinecraftEventHooks.Client;
import net.minecraft.server.WorldStem;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import yesman.epicfight.api.event.Event;

public abstract class WorldLoadEventHook extends Event {
    protected final LevelStorageSource.LevelStorageAccess levelStorageAccess;
    protected final PackRepository packRepository;
    protected final WorldStem stem;
    protected final boolean isNewWorld;

    public WorldLoadEventHook(LevelStorageSource.LevelStorageAccess levelStorage, PackRepository packRepository, WorldStem worldStem, boolean newWorld) {
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


    public static class Initialize extends WorldLoadEventHook implements IEventHook<Initialize> {
        public Initialize(LevelStorageAccess levelStorage, PackRepository packRepository, WorldStem worldStem, boolean newWorld) {
            super(levelStorage, packRepository, worldStem, newWorld);
        }

        @Override
        public Initialize post() {
            return Client.WORLD_INIT.post(this);
        }
    }

    // fires if the player joins an already existing world
    public static class Join extends WorldLoadEventHook implements IEventHook<Join> {
        public Join(LevelStorageAccess levelStorage, PackRepository packRepository, WorldStem worldStem) {
            super(levelStorage, packRepository, worldStem, false);
        }

        @Override
        public Join post() {
            return Client.WORLD_JOIN.post(this);
        }
    }

    // fires if the player creates a new world
    public static class Create extends WorldLoadEventHook implements IEventHook<Create> {
        public Create(LevelStorageAccess levelStorage, PackRepository packRepository, WorldStem worldStem) {
            super(levelStorage, packRepository, worldStem, true);
        }

        @Override
        public Create post() {
            return Client.WORLD_CREATE.post(this);
        }
    }
}
