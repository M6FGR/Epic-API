package dev.m6fgr.epic_api.builders.minecraft;

import com.mojang.brigadier.context.CommandContext;
import dev.m6fgr.epic_api.network.EpicAPINetworkManager;
import dev.m6fgr.epic_api.network.packets.server.SPGameRuleSync;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameRules.Type;
import net.minecraft.world.level.GameRules.Value;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.server.command.EnumArgument;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class GameRulesBuilder {

    private GameRulesBuilder() {}

    public static SynchronizedGameRule<GameRules.BooleanValue> newBoolean(String name, GameRules.Category category, boolean defaultValue, boolean syncToAllPlayers) {
        return new SynchronizedGameRule<>(
                name, category, syncToAllPlayers,
                listener -> GameRules.BooleanValue.create(defaultValue, listener)
        );
    }

    public static SynchronizedGameRule<GameRules.BooleanValue> newBoolean(String name, GameRules.Category category, boolean defaultValue) {
        return newBoolean(name, category, defaultValue, false);
    }

    public static SynchronizedGameRule<GameRules.IntegerValue> newInteger(String name, GameRules.Category category, int defaultValue, boolean syncToAllPlayers) {
        return new SynchronizedGameRule<>(
                name, category, syncToAllPlayers,
                listener -> GameRules.IntegerValue.create(defaultValue, listener)
        );
    }

    public static SynchronizedGameRule<GameRules.IntegerValue> newInteger(String name, GameRules.Category category, int defaultValue) {
        return newInteger(name, category, defaultValue, false);
    }

    public static <E extends Enum<E>> SynchronizedGameRule<EnumValue<E>> newEnum(String name, GameRules.Category category, E defaultValue, boolean syncToAllPlayers) {
        return new SynchronizedGameRule<>(
                name, category, syncToAllPlayers,
                listener -> EnumValue.create(defaultValue, listener)
        );
    }

    public static <E extends Enum<E>> SynchronizedGameRule<EnumValue<E>> newEnum(String name, GameRules.Category category, E defaultValue) {
        return newEnum(name, category, defaultValue, false);
    }


    private static void broadcastToServer(MinecraftServer server, String name, int value) {
        SPGameRuleSync packet = new SPGameRuleSync(name, value);
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            EpicAPINetworkManager.sendToPlayer(player, packet);
        }
    }

    public static class SynchronizedGameRule<V extends Value<V>> {

        private final String name;
        private final GameRules.Category category;
        private final boolean sync;
        private final Function<BiConsumer<MinecraftServer, V>, Type<V>> typeFactory;
        private GameRules.Key<V> key;

        public SynchronizedGameRule(String name, GameRules.Category category, boolean sync, Function<BiConsumer<MinecraftServer, V>, Type<V>> typeFactory) {
            this.name = name;
            this.category = category;
            this.sync = sync;
            this.typeFactory = typeFactory;
        }

        public SynchronizedGameRule<V> register() {
            BiConsumer<MinecraftServer, V> changeListener = (server, value) -> {
                if (this.sync && server != null) {
                    broadcastToServer(server, this.name, value.getCommandResult());
                }
            };

            this.key = GameRules.register(this.name, this.category, this.typeFactory.apply(changeListener));
            return this;
        }

        public V getValue(Level level) {
            return level.getGameRules().getRule(this.key);
        }

        public V getValue(MinecraftServer server) {
            return server.getGameRules().getRule(this.key);
        }


        public GameRules.Key<V> getKey() {
            return this.key;
        }

        public String getName() {
            return this.name;
        }

        public boolean isSynced() {
            return this.sync;
        }
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

        public void setOrdinal(int ordinal, @Nullable MinecraftServer server) {
            this.setOrdinal(ordinal);
            this.onChanged(server);
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
            return create(defaultValue, (server, value) -> {});
        }

        public E get() {
            return this.value;
        }

        @Override
        protected void deserialize(String s) {
            try {
                this.value = Enum.valueOf(this.enumClass, s);
            } catch (IllegalArgumentException ignored) {}
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