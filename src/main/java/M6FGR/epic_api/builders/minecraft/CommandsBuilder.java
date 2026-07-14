package M6FGR.epic_api.builders.minecraft;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
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

    private CommandsBuilder() {}

    public static RootBuilder newRoot(String name) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal(name);
        return new RootBuilder(root);
    }

    // Top-level wrapper
    public static class RootBuilder extends NodeBuilder {
        public RootBuilder(LiteralArgumentBuilder<CommandSourceStack> rootNode) {
            super(rootNode, rootNode);
        }

        @Override
        public RootBuilder executes(Command<CommandSourceStack> cmd) {
            super.executes(cmd);
            return this;
        }

        @Override
        public RootBuilder fork(Consumer<NodeBuilder> branch) {
            super.fork(branch);
            return this;
        }
    }

    // Structural base that passes the root reference downwards
    public static class NodeBuilder {
        protected final ArgumentBuilder<CommandSourceStack, ?> builder;
        protected final LiteralArgumentBuilder<CommandSourceStack> rootReference;

        public NodeBuilder(ArgumentBuilder<CommandSourceStack, ?> builder, LiteralArgumentBuilder<CommandSourceStack> rootReference) {
            this.builder = builder;
            this.rootReference = rootReference;
        }

        public NodeBuilder newLiteral(String name) {
            LiteralArgumentBuilder<CommandSourceStack> next = Commands.literal(name);
            this.builder.then(next);
            return new NodeBuilder(next, this.rootReference);
        }

        public NodeBuilder newArgument(String name, ArgumentType<?> type) {
            RequiredArgumentBuilder<CommandSourceStack, ?> next = Commands.argument(name, type);
            this.builder.then(next);
            return new NodeBuilder(next, this.rootReference);
        }

        public <E extends Enum<E>> EnumCommandBuilder<E> newEnum(String name, Class<E> enumClass) {
            RequiredArgumentBuilder<CommandSourceStack, E> next = Commands.argument(name, EnumArgument.enumArgument(enumClass));
            this.builder.then(next);
            return new EnumCommandBuilder<>(next, this.rootReference, enumClass);
        }

        public IntegerCommandBuilder newInt(String name, int min, int max) {
            RequiredArgumentBuilder<CommandSourceStack, Integer> next = Commands.argument(name, IntegerArgumentType.integer(min, max));
            this.builder.then(next);
            return new IntegerCommandBuilder(next, this.rootReference);
        }

        public FloatCommandBuilder newFloat(String name, float min, float max) {
            RequiredArgumentBuilder<CommandSourceStack, Float> next = Commands.argument(name, FloatArgumentType.floatArg(min, max));
            this.builder.then(next);
            return new FloatCommandBuilder(next, this.rootReference);
        }

        public NodeBuilder executes(Command<CommandSourceStack> cmd) {
            this.builder.executes(cmd);
            return this;
        }

        public NodeBuilder fork(Consumer<NodeBuilder> branch) {
            branch.accept(this);
            return this;
        }

        // Available everywhere! Safely pulls the root literal back out from any depth
        public LiteralArgumentBuilder<CommandSourceStack> build() {
            return this.rootReference;
        }
    }

    public static class EnumCommandBuilder<E extends Enum<E>> extends NodeBuilder {
        protected final Class<E> enumClass;

        public EnumCommandBuilder(ArgumentBuilder<CommandSourceStack, ?> builder, LiteralArgumentBuilder<CommandSourceStack> rootReference, Class<E> enumClass) {
            super(builder, rootReference);
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

    public static class IntegerCommandBuilder extends NodeBuilder {
        public IntegerCommandBuilder(ArgumentBuilder<CommandSourceStack, ?> builder, LiteralArgumentBuilder<CommandSourceStack> rootReference) {
            super(builder, rootReference);
        }

        public IntegerCommandBuilder chainInt(String name, int min, int max, Command<CommandSourceStack> action) {
            var next = Commands.argument(name, IntegerArgumentType.integer(min, max)).executes(action);
            this.builder.then(next);
            return new IntegerCommandBuilder(next, this.rootReference);
        }
    }

    public static class FloatCommandBuilder extends NodeBuilder {
        public FloatCommandBuilder(ArgumentBuilder<CommandSourceStack, ?> builder, LiteralArgumentBuilder<CommandSourceStack> rootReference) {
            super(builder, rootReference);
        }

        public FloatCommandBuilder chainFloat(String name, float min, float max, Command<CommandSourceStack> action) {
            var next = Commands.argument(name, FloatArgumentType.floatArg(min, max)).executes(action);
            this.builder.then(next);
            return new FloatCommandBuilder(next, this.rootReference);
        }
    }
}