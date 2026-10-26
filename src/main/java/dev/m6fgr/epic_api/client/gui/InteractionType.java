package dev.m6fgr.epic_api.client.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Arrays;
import java.util.List;

public enum InteractionType {

    TALK((player, target, ctx) ->
            player.sendSystemMessage(Component.literal("Interacted with " + target.getName().getString()))
    ),

    ATTACK((player, target, ctx) ->
            target.hurt(player.damageSources().playerAttack(player), 2.0F)
    ),

    HEAL((player, target, ctx, args) -> {
        float amount = args.isEmpty() ? 4.0F : (Float) args.getFirst();
        if (target instanceof LivingEntity living) {
            living.heal(amount);
        }
    }),

    OPEN_TRADE((player, target, ctx) -> {
        if (target instanceof Villager villager) {
            villager.setTradingPlayer(player);
            player.openMenu(new SimpleMenuProvider(
                    (containerId, playerInventory, p) -> new MerchantMenu(containerId, playerInventory, villager),
                    villager.getDisplayName()
            ));
        }
    }),

    GIVE_ITEM((player, target, ctx, args) -> {
        if (!args.isEmpty() && args.getFirst() instanceof ItemStack stack) {
            player.addItem(stack.copy());
        }
    }),

    NONE((player, target, ctx) -> {});

    @FunctionalInterface
    public interface ParameterizedHandler {
        void execute(ServerPlayer player, Entity target, IPayloadContext context, List<Object> args);
    }

    @FunctionalInterface
    public interface InteractionHandler {
        void handle(ServerPlayer player, Entity target, IPayloadContext context);
    }

    private ParameterizedHandler parameterizedHandler;
    private InteractionHandler interactionHandler;



    public static InteractionType create(InteractionHandler handler) {
        NONE.interactionHandler = handler;
        return NONE;
    }

    public static InteractionType createParameterized(ParameterizedHandler handler) {
        NONE.parameterizedHandler = handler;
        return NONE;
    }

    InteractionType(ParameterizedHandler handler) {
        this.parameterizedHandler = handler;
        this.interactionHandler = null;
    }

    InteractionType(InteractionHandler handler) {
        this.interactionHandler = handler;
        this.parameterizedHandler = null;
    }

    public InteractionType withArgs(Object... arguments) {
        new BoundInteraction(this, Arrays.asList(arguments));
        return this;
    }

    public void execute(ServerPlayer player, Entity target, IPayloadContext context) {
        execute(player, target, context, List.of());
    }

    public void execute(ServerPlayer player, Entity target, IPayloadContext context, List<Object> args) {
        if (this.parameterizedHandler != null) {
            this.parameterizedHandler.execute(player, target, context, args);
        }
        if (this.interactionHandler != null) {
            this.interactionHandler.handle(player, target, context);
        }
    }

    public record BoundInteraction(InteractionType type, List<Object> arguments) {
        public void execute(ServerPlayer player, Entity target, IPayloadContext context) {
            if (this.type != null) {
                this.type.execute(player, target, context, this.arguments);
            }
        }
    }
}