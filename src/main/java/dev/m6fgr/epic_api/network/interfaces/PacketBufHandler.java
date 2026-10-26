package dev.m6fgr.epic_api.network.interfaces;

import dev.m6fgr.epic_api.utils.code.AccessType;
import dev.m6fgr.epic_api.utils.code.CodeUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamMemberEncoder;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface PacketBufHandler extends CustomPacketPayload {

    @FunctionalInterface
    interface StreamMemberDecoder<O, T> {
        T decode(T instance, O output);
    }

    static <B extends ByteBuf, T extends CustomPacketPayload> StreamCodec<B, T> codecOf(
            Class<T> packetCls,
            StreamMemberEncoder<B, T> encoder,
            StreamMemberDecoder<B, T> decoder
    ) {
        return new StreamCodec<>() {
            @Override
            public T decode(B buffer) {
                T instance = CodeUtils.newEmptyInstance(packetCls, AccessType.DECLARED);
                return decoder.decode(instance, buffer);
            }

            @Override
            public void encode(B buffer, T value) {
                encoder.encode(value, buffer);
            }
        };
    }
}