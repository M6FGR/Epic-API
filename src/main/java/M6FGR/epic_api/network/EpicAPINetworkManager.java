package M6FGR.epic_api.network;

import M6FGR.epic_api.cls.ILoadableClass;
import M6FGR.epic_api.main.EpicAPI;
import M6FGR.epic_api.network.packets.server.SPGameRuleSync;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import javax.annotation.Nullable;

public class EpicAPINetworkManager implements ILoadableClass {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INS =
            NetworkRegistry.newSimpleChannel(
                    EpicAPI.identifier("network_manager"),
                    () -> PROTOCOL_VERSION,
                    PROTOCOL_VERSION::equals,
                    PROTOCOL_VERSION::equals
            );

    private void registerPackets() {
        int id = 0;
        INS.registerMessage(id++, SPGameRuleSync.class, SPGameRuleSync::encode, SPGameRuleSync::decode, SPGameRuleSync::handle);
    }

    @Override
    public void onModCommonEvents(FMLCommonSetupEvent commonEvent) {
        commonEvent.enqueueWork(this::registerPackets);
    }

    /**
     * Sends a packet using the specified distribution method.
     */
    public static <PCT> void sendTo(PCT packet, Distribute type) {
        type.send(packet, null);
    }

    public static <PCT> void send(PCT packet) {
        sendTo(packet, Distribute.SERVER);
    }

    public static <PCT> void receive(PCT packet) {
        sendTo(packet, Distribute.ALL_CLIENTS);
    }


    public enum Distribute {
        SERVER {
            @Override
            public <PCT> void send(PCT packet, @Nullable ServerPlayer target) {
                INS.sendToServer(packet);
            }
        },
        ALL_CLIENTS {
            @Override
            public <PCT> void send(PCT packet, @Nullable ServerPlayer target) {
                INS.send(PacketDistributor.ALL.noArg(), packet);
            }
        },
        PLAYER {
            @Override
            public <PCT> void send(PCT packet, @Nullable ServerPlayer target) {
                if (target != null) {
                    INS.send(PacketDistributor.PLAYER.with(() -> target), packet);
                } else {
                    EpicAPI.warn("Attempted to send PLAYER packet without a target player!");
                }
            }
        };

        abstract <PCT> void send(PCT packet, @Nullable ServerPlayer target);
    }
    /**
     * Specific helper for sending to a single player (Server -> Client)
     */
    public static <PCT> void sendToPlayer(ServerPlayer player, PCT packet) {
        INS.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }
}