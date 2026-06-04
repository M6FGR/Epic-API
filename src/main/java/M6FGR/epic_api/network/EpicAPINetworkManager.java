package M6FGR.epic_api.network;

import M6FGR.epic_api.cls.ILoadableClass;
import M6FGR.epic_api.network.packets.server.SPGameRulesSync;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.network.EntityPairingPacketType;
import yesman.epicfight.network.server.SPEntityPairingPacket;

public class EpicAPINetworkManager implements ILoadableClass {
    private static final String PROTOCOL = "1";
    private PayloadRegistrar registrar;

    private void registerNetworking(final RegisterPayloadHandlersEvent event) {
        this.registrar = event.registrar(PROTOCOL);
        this.play(SPGameRulesSync.TYPE, SPGameRulesSync.CODEC, SPGameRulesSync::handle, PayloadType.SERVER);
    }

    private <T extends CustomPacketPayload> void play(Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, IPayloadHandler<T> handler, PayloadType payloadType) {
        switch (payloadType) {
            case CLIENT -> this.registrar.playToClient(type, codec, handler);
            case SERVER -> this.registrar.playToServer(type, codec, handler);
        }
    }


    public static <P extends CustomPacketPayload> void send(P packet, Distribute type) {
        sendTo(type, packet, null, null, null);
    }

    public static <P extends CustomPacketPayload> void send(P packet, Entity entity, Distribute type) {
        sendTo(type, packet, null, entity, null);
    }

    public static <P extends CustomPacketPayload> void send(P packet, ServerPlayer player, Distribute type) {
        sendTo(type, packet, player, null, null);
    }

    public static <P extends CustomPacketPayload> void send(P packet, ServerLevel level, Distribute type) {
        sendTo(type, packet, null, null, level);
    }

    public static <EP extends EntityPairingPacketType> void sendPairingPacket(ServerPlayer player, EP packet, Object... args) {
        sendPairingPacket(player, packet, Distribute.ALL_CLIENTS, args);
    }

    public static <EP extends EntityPairingPacketType> void sendPairingPacket(ServerPlayer player, EP packet, Distribute distribute, Object... args) {
        SPEntityPairingPacket entityPairingPacket = new SPEntityPairingPacket(player.getId(), packet);
        FriendlyByteBuf buffer = entityPairingPacket.buffer();

        for (Object arg : args) {
            if (arg == null) {
                buffer.writeBoolean(false);
                continue;
            }

            switch (arg) {
                case Integer i    -> buffer.writeInt(i);
                case Float f      -> buffer.writeFloat(f);
                case Double d     -> buffer.writeDouble(d);
                case Boolean b    -> buffer.writeBoolean(b);
                case String s     -> buffer.writeUtf(s);
                case Long l       -> buffer.writeLong(l);
                case Short s      -> buffer.writeShort(s);
                case Byte b       -> buffer.writeByte(b);
                case Enum<?> e    -> buffer.writeEnum(e);
                case BlockPos pos -> buffer.writeBlockPos(pos);
                case Vec3 vec     -> {
                    buffer.writeDouble(vec.x);
                    buffer.writeDouble(vec.y);
                    buffer.writeDouble(vec.z);
                }
                default -> throw new IllegalArgumentException("Unsupported network packet argument type: " + arg.getClass().getName());
            }
        }

        send(entityPairingPacket, distribute);
    }

    public static <EP extends EntityPairingPacketType> void sendPairingPacket(ServerPlayer player, EP packet, Distribute distribute) {
        SPEntityPairingPacket entityPairingPacket = new SPEntityPairingPacket(player.getId(), packet);
        send(entityPairingPacket, distribute);
    }

    private static <PCT extends CustomPacketPayload> void sendTo(Distribute distribute, PCT packet, @Nullable ServerPlayer player, @Nullable Entity entity, @Nullable ServerLevel level) {
        switch (distribute) {
            case PLAYER -> {
                if (player != null) PacketDistributor.sendToPlayer(player, packet);
            }
            case PTEAS -> {
               if (entity != null) {
                   PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, packet);
               } else if (player != null) {
                   PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, packet);
               }
            }
            case PTE -> {
                if (entity != null) {
                    PacketDistributor.sendToPlayersTrackingEntity(entity, packet);
                } else if (player != null) {
                    PacketDistributor.sendToPlayersTrackingEntity(player, packet);
                }
            }
            case WORLD_PLAYERS -> {
                if (level != null) PacketDistributor.sendToPlayersInDimension(level, packet);
            }
            case SERVER -> {
                PacketDistributor.sendToServer(packet);
            }
            case ALL_CLIENTS -> {
                PacketDistributor.sendToAllPlayers(packet);
            }
        }
    }

    public enum Distribute {
        SERVER,
        ALL_CLIENTS,
        PLAYER,
        WORLD_PLAYERS,
        PTE,
        PTEAS
    }

    private enum PayloadType {
        CLIENT,
        SERVER
    }

    @Override
    public void onModConstructor(IEventBus modBus) {
        modBus.addListener(this::registerNetworking);
    }
}