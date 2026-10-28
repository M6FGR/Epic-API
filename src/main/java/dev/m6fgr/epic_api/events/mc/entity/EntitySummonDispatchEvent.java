package dev.m6fgr.epic_api.events.mc.entity;

import dev.m6fgr.epic_api.events.dispatch.IDispatchableEvent;
import dev.m6fgr.epic_api.events.dispatch.abstracts.DispatchEvent;
import dev.m6fgr.epic_api.events.mc.MinecraftDispatchableEvents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public abstract class EntitySummonDispatchEvent extends DispatchEvent implements IDispatchableEvent<EntitySummonDispatchEvent> {

    private final Entity entity;
    private final Level level;
    private final boolean fromDisk;

    public EntitySummonDispatchEvent(Entity entity, Level level, boolean isFromDisk) {
        this.entity = entity;
        this.level = level;
        this.fromDisk = isFromDisk;
    }

    public EntitySummonDispatchEvent(Entity entity, Level level) {
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


    public static class Server extends EntitySummonDispatchEvent {
        public Server(Entity entity, ServerLevel level) {
            super(entity, level);
        }

        @Override
        public Server post() {
            return MinecraftDispatchableEvents.Entity.SUMMON_SERVER.postEvent(this);
        }
    }

    public static class Client extends EntitySummonDispatchEvent {
        public Client(Entity entity, ClientLevel level) {
            super(entity, level);
        }

        @Override
        public Client post() {
            return MinecraftDispatchableEvents.Entity.SUMMON_CLIENT.postEvent(this);
        }
    }
}
