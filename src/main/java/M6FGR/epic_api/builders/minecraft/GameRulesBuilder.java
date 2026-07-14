package M6FGR.epic_api.builders.minecraft;

import M6FGR.epic_api.network.EpicAPINetworkManager;
import M6FGR.epic_api.network.EpicAPINetworkManager.Distribute;
import M6FGR.epic_api.network.packets.server.SPGameRulesSync;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameRules.Value;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.server.command.EnumArgument;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;

public class GameRulesBuilder {

    private GameRulesBuilder() {}

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
        SPGameRulesSync gameRulesSyncPct = new SPGameRulesSync(name, value);
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            EpicAPINetworkManager.send(gameRulesSyncPct, player, Distribute.PLAYER);
        }
    }


    public static <T extends Value<T>> T getRuleValue(Level level, GameRules.Key<T> key) {
        return level.getGameRules().getRule(key);
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