package M6FGR.epic_api.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.NotNull;

@Experimental
public interface IClientPacket<CP extends CustomPacketPayload> extends CustomPacketPayload {

    Type<CP> getType();

    StreamCodec<FriendlyByteBuf, CP> getCodec();

    @Override
    default @NotNull Type<? extends CustomPacketPayload> type() {
        return this.getType();
    }

    void handle(IPayloadContext context);
}
