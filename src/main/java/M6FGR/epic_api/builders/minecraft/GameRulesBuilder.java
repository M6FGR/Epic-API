package M6FGR.epic_api.builders.minecraft;

import M6FGR.epic_api.cls.ILoadableClass;
import M6FGR.epic_api.main.EpicAPI;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameRules.BooleanValue;
import net.minecraft.world.level.GameRules.IntegerValue;
import net.minecraft.world.level.GameRules.Value;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.server.command.EnumArgument;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.BiConsumer;

public class GameRulesBuilder implements ILoadableClass {
    private static final ResourceLocation PACKET_ID = EpicAPI.identifier("gamerule_sync");

    // Non-Synchronized methods (by default)
    public static <E extends Enum<E>> GameRules.Key<EnumValue<E>> newEnum(String name, GameRules.Category category, E defaultValue) {
        return newEnum(name, category, defaultValue, false);
    }

    public static GameRules.Key<GameRules.BooleanValue> newBoolean(String name, GameRules.Category category, boolean defaultValue) {
        return newBoolean(name, category, defaultValue, false);
    }

    public static GameRules.Key<GameRules.IntegerValue> newInteger(String name, GameRules.Category category, int defaultValue) {
        return newInteger(name, category, defaultValue, false);
    }

    // Synchronized methods
    public static GameRules.Key<GameRules.BooleanValue> newBoolean(String name, GameRules.Category category, boolean defaultValue, boolean synchronised) {
        if (synchronised) {
            return GameRules.register(name, category, GameRules.BooleanValue.create(defaultValue, (server, booleanValue) -> {
                if (server != null) broadcastToServer(server, name, booleanValue.getCommandResult());
            }));
        }
        return GameRules.register(name, category, GameRules.BooleanValue.create(defaultValue));
    }


    public static <E extends Enum<E>> GameRules.Key<EnumValue<E>> newEnum(String name, GameRules.Category category, E defaultValue, boolean synchronise) {
        if (synchronise) {
            return GameRules.register(name, category, EnumValue.create(defaultValue, (server, enumValue) -> {
                if (server != null) broadcastToServer(server, name, enumValue.get().ordinal());
            }));
        }
        return GameRules.register(name, category, EnumValue.create(defaultValue));
    }

    public static GameRules.Key<GameRules.IntegerValue> newInteger(String name, GameRules.Category category, int defaultValue, boolean synchronise) {
        if (synchronise) {
            return GameRules.register(name, category, GameRules.IntegerValue.create(defaultValue, (server, value) -> {
                if (server != null) broadcastToServer(server, name, value.get());
            }));
        }
        return GameRules.register(name, category, GameRules.IntegerValue.create(defaultValue));
    }


    // Syncing the commands
    private static void broadcastToServer(MinecraftServer server, String name, int value) {
        SPGameRulesSync payload = new SPGameRulesSync(name, value);
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.connection.send(new ClientboundCustomPayloadPacket(payload));
        }
    }

    private record SPGameRulesSync(String ruleName, int value) implements CustomPacketPayload {
        public static final Type<SPGameRulesSync> TYPE = new Type<>(PACKET_ID);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        public static final StreamCodec<FriendlyByteBuf, SPGameRulesSync> CODEC = CustomPacketPayload.codec(
                (payload, buffer) -> {
                    buffer.writeUtf(payload.ruleName);
                    buffer.writeInt(payload.value);
                },
                buffer -> new SPGameRulesSync(buffer.readUtf(), buffer.readInt())
        );
    }

    private void registerNetworking(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("efa");
        registrar.playToClient(SPGameRulesSync.TYPE, SPGameRulesSync.CODEC, (payload, context) -> {
            context.enqueueWork(() -> {
                Minecraft mc = Minecraft.getInstance();
                if (mc.level != null) {
                    GameRules.visitGameRuleTypes(new GameRules.GameRuleTypeVisitor() {
                        @Override
                        @ParametersAreNonnullByDefault
                        public <T extends GameRules.Value<T>> void visit(GameRules.Key<T> key, GameRules.Type<T> type) {
                            if (key.toString().equals(payload.ruleName())) {
                                T rule = mc.level.getGameRules().getRule(key);
                                switch (rule) {
                                    case BooleanValue bool -> bool.set(payload.value() != 0, null);
                                    case IntegerValue intRule -> intRule.set(payload.value(), null);
                                    case EnumValue<?> enumRule -> enumRule.setOrdinal(payload.value());
                                    default -> {
                                    }
                                }
                            }
                        }
                    });
                }
            });
        });
    }

    // --- Accessors ---
    public static boolean getBoolVal(Level level, GameRules.Key<GameRules.BooleanValue> key) {
        return level.getGameRules().getBoolean(key);
    }

    public static int getIntVal(Level level, GameRules.Key<GameRules.IntegerValue> key) {
        return level.getGameRules().getInt(key);
    }

    public static <E extends Enum<E>> E getEnumVal(Level level, GameRules.Key<EnumValue<E>> key) {
        return level.getGameRules().getRule(key).get();
    }

    @Override
    public void onModConstructor(IEventBus modBus) {
        modBus.addListener(this::registerNetworking);
        EpicAPI.debug("Registered SPGameRuleSync Packet.");

    }

    public static class EnumValue<E extends Enum<E>> extends Value<EnumValue<E>> {

        private final Class<E> enumClass;
        private E value;

        public EnumValue(GameRules.Type<EnumValue<E>> type, Class<E> enumClass, E value) {
            super(type);
            this.enumClass = enumClass;
            this.value = value;
        }

        public void setOrdinal(int ordinal) {
            this.value = this.enumClass.getEnumConstants()[ordinal];
        }

        public static <E extends Enum<E>> GameRules.Type<EnumValue<E>> create(E defaultValue, BiConsumer<MinecraftServer, EnumValue<E>> listener) {
            Class<E> clazz = defaultValue.getDeclaringClass();

            return new GameRules.Type<>(
                    () -> EnumArgument.enumArgument(clazz),
                    (type) -> new EnumValue<>(type, clazz, defaultValue),
                    listener,
                    GameRules.GameRuleTypeVisitor::visit
            );
        }

        public static <E extends Enum<E>> GameRules.Type<EnumValue<E>> create(E defaultValue) {
            return create(defaultValue, (s, e) -> {
            });
        }

        public E get() {
            return this.value;
        }

        @Override
        protected void deserialize(String s) {
            this.value = Enum.valueOf(this.enumClass, this.value.name());
        }

        @Override
        protected void updateFromArgument(CommandContext<CommandSourceStack> ctx, String name) {
            this.value = ctx.getArgument(name, this.enumClass);
        }

        @Override
        public @NotNull String serialize() {
            return this.value.name();
        }

        @Override
        public int getCommandResult() {
            return this.value.ordinal();
        }

        @Override
        protected EnumValue<E> getSelf() {
            return this;
        }

        @Override
        protected EnumValue<E> copy() {
            return new EnumValue<>(this.type, this.enumClass, this.value);
        }

        @Override
        public void setFrom(EnumValue<E> other, @Nullable MinecraftServer server) {
            this.value = other.value;
            this.onChanged(server);
        }
    }
}