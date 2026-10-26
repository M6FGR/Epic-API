package dev.m6fgr.epic_api.network.packets.client;

import dev.m6fgr.epic_api.main.EpicAPI;
import dev.m6fgr.epic_api.network.interfaces.PacketBufHandler;
import dev.m6fgr.epic_api.network.packets.ICustomPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record CPFreezeEntity(int entityId, boolean freeze) implements ICustomPacket<CPFreezeEntity> {

    private CPFreezeEntity() {
        this(0, false);
    }

    private CPFreezeEntity(FriendlyByteBuf byteBuf) {
        this(byteBuf.readInt(), byteBuf.readBoolean());
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.entityId);
        buf.writeBoolean(this.freeze);
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, CPFreezeEntity> codec() {
        return PacketBufHandler.codecOf(CPFreezeEntity.class, CPFreezeEntity::encode, CPFreezeEntity::decode);
    }

    @Override
    public CPFreezeEntity decode(FriendlyByteBuf buf) {
        return new CPFreezeEntity(buf);
    }

    @Override
    public CustomPacketPayload.@NotNull Type<CPFreezeEntity> type() {
        return new CustomPacketPayload.Type<>(EpicAPI.getInstance().identifier("freeze_entity"));
    }

    @Override
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                Entity target = serverPlayer.level().getEntity(this.entityId());
                if (target instanceof Mob mob) {
                    if (this.freeze && !mob.isFallFlying()) {
                        mob.getNavigation().stop();
                        mob.setDeltaMovement(0, mob.getDeltaMovement().y, 0);
                        mob.stopUsingItem();
                        mob.setNoAi(true);
                    } else {
                        mob.setNoAi(false);
                    }
                }
            }
        });
    }
}
