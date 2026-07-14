package M6FGR.epic_api.events.mc.entity;

import M6FGR.epic_api.events.IEventHook;
import M6FGR.epic_api.events.mc.MinecraftEventHooks;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import yesman.epicfight.api.event.Event;

public abstract class EntitySummonEventHook extends Event {

    private final Entity entity;
    private final Level level;
    private final boolean fromDisk;

    public EntitySummonEventHook(Entity entity, Level level, boolean isFromDisk) {
        this.entity = entity;
        this.level = level;
        this.fromDisk = isFromDisk;
    }

    public EntitySummonEventHook(Entity entity, Level level) {
        this(entity, level, false);
    }

    public Entity getEntity() {
        return this.entity;
    }

    public boolean isFromDisk() {
        return this.fromDisk;
    }

    public Level getLevel() {
        return this.level;
    }


    public static class Server extends EntitySummonEventHook implements IEventHook<Server> {
        public Server(Entity entity, ServerLevel level) {
            super(entity, level);
        }

        @Override
        public Server post() {
            return MinecraftEventHooks.Entity.SUMMON_SERVER.post(this);
        }
    }

    public static class Client extends EntitySummonEventHook implements IEventHook<Client> {
        public Client(Entity entity, ClientLevel level) {
            super(entity, level);
        }

        @Override
        public Client post() {
            return MinecraftEventHooks.Entity.SUMMON_CLIENT.post(this);
        }
    }
}
