package M6FGR.epic_api.builders.minecraft;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.neoforged.neoforge.server.command.EnumArgument;
import org.jetbrains.annotations.ApiStatus.Experimental;

import java.util.function.Consumer;
@Experimental
public class CommandsBuilder {

    // Entry point for /command
    public static RootBuilder newRoot(String name) {
        return new RootBuilder(Commands.literal(name));
    }

    public static class RootBuilder {
        protected final ArgumentBuilder<CommandSourceStack, ?> builder;

        public RootBuilder(ArgumentBuilder<CommandSourceStack, ?> builder) {
            this.builder = builder;
        }

        public RootBuilder newLiteral(String name) {
            LiteralArgumentBuilder<CommandSourceStack> next = Commands.literal(name);
            this.builder.then(next);
            return new RootBuilder(next);
        }

        public <E extends Enum<E>> EnumCommandBuilder<E> newEnum(String name, Class<E> enumClass) {
            RequiredArgumentBuilder<CommandSourceStack, E> next = Commands.argument(name, EnumArgument.enumArgument(enumClass));
            this.builder.then(next);
            return new EnumCommandBuilder<>(next, enumClass);
        }

        public IntegerCommandBuilder newInt(String name, int min, int max) {
            RequiredArgumentBuilder<CommandSourceStack, Integer> next = Commands.argument(name, IntegerArgumentType.integer(min, max));
            this.builder.then(next);
            return new IntegerCommandBuilder(next);
        }

        public FloatCommandBuilder newFloat(String name, float min, float max) {
            RequiredArgumentBuilder<CommandSourceStack, Float> next = Commands.argument(name, FloatArgumentType.floatArg(min, max));
            this.builder.then(next);
            return new FloatCommandBuilder(next);
        }

        public RootBuilder executes(Command<CommandSourceStack> cmd) {
            this.builder.executes(cmd);
            return this;
        }

        public RootBuilder fork(Consumer<RootBuilder> branch) {
            branch.accept(this);
            return this;
        }

        @SuppressWarnings("unchecked")
        public LiteralArgumentBuilder<CommandSourceStack> build() {
            // Traverse back or ensure root is usually returned, users keep the reference to the first RootBuilder
            return (LiteralArgumentBuilder<CommandSourceStack>) this.builder;
        }
    }

    public static class EnumCommandBuilder<E extends Enum<E>> extends RootBuilder {
        protected final Class<E> enumClass;

        public EnumCommandBuilder(ArgumentBuilder<CommandSourceStack, ?> builder, Class<E> enumClass) {
            super(builder);
            this.enumClass = enumClass;
        }

        public EnumCommandBuilder<E> withEnum(String name) {
            RequiredArgumentBuilder<CommandSourceStack, E> next = Commands.argument(name, EnumArgument.enumArgument(this.enumClass));
            this.builder.then(next);
            return this;
        }

        public EnumCommandBuilder<E> newTargets(String name, Command<CommandSourceStack> action) {
            RequiredArgumentBuilder<CommandSourceStack, EntitySelector> next = Commands.argument(name, EntityArgument.entities()).executes(action);
            this.builder.then(next);
            return this;
        }
    }

    public static class IntegerCommandBuilder extends RootBuilder {
        public IntegerCommandBuilder(ArgumentBuilder<CommandSourceStack, ?> builder) {
            super(builder);
        }

        public IntegerCommandBuilder chainInt(String name, int min, int max, Command<CommandSourceStack> action) {
            var next = Commands.argument(name, IntegerArgumentType.integer(min, max)).executes(action);
            this.builder.then(next);
            return new IntegerCommandBuilder(next);
        }
    }

    public static class FloatCommandBuilder extends RootBuilder {
        public FloatCommandBuilder(ArgumentBuilder<CommandSourceStack, ?> builder) {
            super(builder);
        }

        public FloatCommandBuilder chainFloat(String name, float min, float max, Command<CommandSourceStack> action) {
            var next = Commands.argument(name, FloatArgumentType.floatArg(min, max)).executes(action);
            this.builder.then(next);
            return new FloatCommandBuilder(next);
        }
    }
}