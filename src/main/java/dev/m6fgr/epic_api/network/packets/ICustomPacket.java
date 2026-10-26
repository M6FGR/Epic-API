package dev.m6fgr.epic_api.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public interface ICustomPacket<T extends CustomPacketPayload> extends CustomPacketPayload {
    @Override
    Type<T> type();

    StreamCodec<? super RegistryFriendlyByteBuf, T> codec();

    void encode(FriendlyByteBuf buf);

    T decode(FriendlyByteBuf buf);

    void handle(IPayloadContext context);
}
