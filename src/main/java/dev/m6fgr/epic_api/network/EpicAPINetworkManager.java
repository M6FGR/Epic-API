package dev.m6fgr.epic_api.network;

import dev.m6fgr.epic_api.cls.loadable.ILoadableClass;
import dev.m6fgr.epic_api.cls.loadable.marks.Invoke;
import dev.m6fgr.epic_api.network.packets.EpicAPIPacketsManager;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import yesman.epicfight.network.EntityPairingPacketType;
import yesman.epicfight.network.server.SPEntityPairingPacket;

public class EpicAPINetworkManager implements ILoadableClass {

    @Invoke
    public EpicAPINetworkManager(IEventBus modBus) {
        modBus.addListener(this::registerNetworking);
    }

    private void registerNetworking(final RegisterPayloadHandlersEvent event) {
        EpicAPIPacketsManager.forEachPacketType(event.registrar("1.0"));
    }

    public static void sendToServer(CustomPacketPayload payload) {
        PacketDistributor.sendToServer(payload);
    }

    public static void sendToAllPlayers(CustomPacketPayload payload) {
        PacketDistributor.sendToAllPlayers(payload);
    }

    public static void sendToAllTrackingThisEntity(Entity entity, CustomPacketPayload payload) {
        PacketDistributor.sendToPlayersTrackingEntity(entity, payload);
    }

    public static void sendToAllTrackingThisEntityAndSelf(Entity entity, CustomPacketPayload payload) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, payload);
    }

    public static void sendToPlayer(ServerPlayer serverPlayer, CustomPacketPayload payload) {
        PacketDistributor.sendToPlayer(serverPlayer, payload);
    }

    public static void sendToAllPlayersInChunk(ServerLevel serverLevel, ChunkPos chunkPos, CustomPacketPayload payload) {
        PacketDistributor.sendToPlayersTrackingChunk(serverLevel, chunkPos, payload);
    }

    public static void sendPairingPacket(Entity entity, EntityPairingPacketType pairingType, Object... arguments) {
        SPEntityPairingPacket entityPairPacket = new SPEntityPairingPacket(entity.getId(), pairingType);
        for (Object argument : arguments) {
            switch (argument) {
                case Integer integer -> entityPairPacket.buffer().writeInt(integer);
                case Float flt -> entityPairPacket.buffer().writeFloat(flt);
                case Long lng -> entityPairPacket.buffer().writeLong(lng);
                case Short shrt -> entityPairPacket.buffer().writeShort(shrt);
                case Byte byt -> entityPairPacket.buffer().writeByte(byt);
                case Boolean bool -> entityPairPacket.buffer().writeBoolean(bool);
                case Enum<?> enumCls -> entityPairPacket.buffer().writeEnum(enumCls);
                default -> throw new IllegalStateException("Unexpected argument: " + argument);
            }
        }
        EpicAPINetworkManager.sendToAllTrackingThisEntityAndSelf(entity, entityPairPacket);
    }
}