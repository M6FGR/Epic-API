package dev.m6fgr.epic_api.network.packets.client;

import dev.m6fgr.epic_api.client.gui.InteractionType;
import dev.m6fgr.epic_api.main.EpicAPI;
import dev.m6fgr.epic_api.main.EpicAPIMod;
import dev.m6fgr.epic_api.network.interfaces.PacketBufHandler;
import dev.m6fgr.epic_api.network.packets.ICustomPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record CPEntityInteraction(int targetEntityId, InteractionType interactionType) implements ICustomPacket<CPEntityInteraction> {

    private CPEntityInteraction() {
        this(0, InteractionType.NONE);
    }

    @Override
    public CustomPacketPayload.@NotNull Type<CPEntityInteraction> type() {
        return new CustomPacketPayload.Type<>(EpicAPI.getInstance().identifier("entity_interaction"));
    }

    private CPEntityInteraction(FriendlyByteBuf byteBuf) {
        this(byteBuf.readInt(), byteBuf.readEnum(InteractionType.class));
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.targetEntityId);
        buf.writeEnum(this.interactionType);
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, CPEntityInteraction> codec() {
        return PacketBufHandler.codecOf(CPEntityInteraction.class, CPEntityInteraction::encode, CPEntityInteraction::decode);
    }

    @Override
    public CPEntityInteraction decode(FriendlyByteBuf buf) {
        return new CPEntityInteraction(buf.readInt(), buf.readEnum(InteractionType.class));
    }

    @Override
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                Entity target = player.level().getEntity(this.targetEntityId);
                if (target != null && target.isAlive() && player.distanceToSqr(target) <= 64.0) {
                    this.interactionType.execute(player, target, context);
                    EpicAPIMod.debugIfDevSide("Executed interaction: {}", this.interactionType.toString().toLowerCase());
                }
            }
        });
    }
}