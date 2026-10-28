package dev.m6fgr.epic_api.network.packets;

import dev.m6fgr.epic_api.main.EpicAPIMod;
import dev.m6fgr.epic_api.network.packets.client.CPEntityInteraction;
import dev.m6fgr.epic_api.network.packets.client.CPFreezeEntity;
import dev.m6fgr.epic_api.network.packets.server.SPGameRuleSync;
import dev.m6fgr.epic_api.utils.code.AccessType;
import dev.m6fgr.epic_api.utils.code.CodeUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.ArrayList;
import java.util.List;

public class EpicAPIPacketsManager {
    private static final List<ICustomPacket<?>> CLIENT_PACKETS = new ArrayList<>();
    private static final List<ICustomPacket<?>> SERVER_PACKETS = new ArrayList<>();
    private static final List<ICustomPacket<?>> BIDIRECTIONAL_PACKETS = new ArrayList<>();


    public static <T extends ICustomPacket<T>> void register(Class<T> packet) {
        String packetName = packet.getSimpleName();
        String packetType = "";
        ICustomPacket<?> customPacket = CodeUtils.newInstance(packet, AccessType.DECLARED);

        if (nameStartsOrContains(packetName, "SP", "SB", "STC", "ServerBound", "ServerPacket")) {
            SERVER_PACKETS.add(customPacket);
            packetType = "server";
        }
        if (nameStartsOrContains(packetName, "CP", "CB", "CTS", "ClientBound", "ClientPacket")) {
            CLIENT_PACKETS.add(customPacket);
            packetType = "client";
        }
        if (nameStartsOrContains(packetName, "BDP", "BI", "BP", "BiDirectionalBound", "BiDirectionalPacket")) {
            BIDIRECTIONAL_PACKETS.add(customPacket);
            packetType = "bidirectional";
        }
        EpicAPIMod.debugIfDevSide("Registered {} packet {}", packetType, packetName);
    }

    static {
        register(SPGameRuleSync.class);
        register(CPEntityInteraction.class);
        register(CPFreezeEntity.class);
    }

    @SuppressWarnings("unchecked")
    public static <T extends CustomPacketPayload> void forEachPacketType(PayloadRegistrar registrar) {
        CLIENT_PACKETS.forEach(iCustomPacket ->
                registrar.playToServer(
                        (Type<T>) iCustomPacket.type(),
                        (StreamCodec<? super RegistryFriendlyByteBuf, T>) iCustomPacket.codec(),
                        (packet, context) -> iCustomPacket.handle(context))
        );
        SERVER_PACKETS.forEach(iCustomPacket ->
                registrar.playToClient(
                        (Type<T>) iCustomPacket.type(),
                        (StreamCodec<? super RegistryFriendlyByteBuf, T>) iCustomPacket.codec(),
                        (packet, context) -> iCustomPacket.handle(context))
        );
        BIDIRECTIONAL_PACKETS.forEach(iCustomPacket ->
                registrar.playBidirectional(
                        (Type<T>) iCustomPacket.type(),
                        (StreamCodec<? super RegistryFriendlyByteBuf, T>) iCustomPacket.codec(),
                        (packet, context) -> iCustomPacket.handle(context))
        );
    }

    private static boolean nameStartsOrContains(String name, String... matchings) {
        for (String word : matchings) {
            if (name.startsWith(word) || name.contains(word)) {
                return true;
            }
        }
        return false;
    }
}
