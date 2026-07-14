package M6FGR.epic_api.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public interface ICustomPacket<T extends CustomPacketPayload> extends CustomPacketPayload {
    Type<T> getType();

    StreamCodec<FriendlyByteBuf, T> getCodec();

    @Override
    default Type<? extends CustomPacketPayload> type() {
        return this.getType();
    }

    void handle(IPayloadContext context);
}
