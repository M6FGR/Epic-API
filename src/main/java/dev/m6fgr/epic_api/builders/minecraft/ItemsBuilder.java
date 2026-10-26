package dev.m6fgr.epic_api.builders.minecraft;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.UnaryOperator;

public class ItemsBuilder {

    private final DeferredRegister.Items rawRegistry;
    private final String modID;

    private ItemsBuilder(String modID) {
        this.modID = modID;
        this.rawRegistry = DeferredRegister.createItems(modID);
    }

    private static boolean usedBuilderRegistry = false;

    public static ItemsBuilder createRegistry(String modID) {
        usedBuilderRegistry = true;
        return new ItemsBuilder(modID);
    }

    /**
     * Registers a new item with easy property manipulation.
     * @param itemID Unique identifier for the item.
     * @param constructor The constructor reference (e.g., MyItem::new).
     * @param propertyModifier A lambda to adjust item properties (e.g., p -> p.stacksTo(1)).
     */
    public <T extends Item> DeferredHolder<Item, T> newItem(
            String itemID,
            Function<Item.Properties, T> constructor,
            UnaryOperator<Item.Properties> propertyModifier
    ) {
        if (!usedBuilderRegistry) {
            throw new IllegalArgumentException("Cannot register items for: " + this.modID + ", Use ItemsBuilder.createRegistry() instead of DeferredRegister.create()!");
        }
        return this.rawRegistry.register(itemID, () -> {
            Item.Properties props = propertyModifier.apply(new Item.Properties());
            return constructor.apply(props);
        });
    }


    public void register(IEventBus modBus) {
        this.rawRegistry.register(modBus);
    }


    // Overload for simple items that don't need custom properties
    public <T extends Item> DeferredHolder<Item, T> newItem(String itemID, Function<Item.Properties, T> constructor) {
        return newItem(itemID, constructor, p -> p);
    }
}