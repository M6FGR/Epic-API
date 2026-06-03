package M6FGR.epic_api.builders.minecraft;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.UnaryOperator;

public class ItemsBuilder {

    private ItemsBuilder() {}

    private static DeferredRegister<Item> registry;
    private static boolean usedBuilderRegistry = false;


    public static DeferredRegister<Item> buildRegistry(String modid) {
        registry = DeferredRegister.create(Registries.ITEM, modid);
        usedBuilderRegistry = true;
        return registry;
    }


    /**
     * Registers a new item with easy property manipulation.
     * @param itemId Unique identifier for the item.
     * @param constructor The constructor reference (e.g., MyItem::new).
     * @param propertyModifier A lambda to adjust item properties (e.g., p -> p.stacksTo(1)).
     */
    public static <T extends Item> DeferredHolder<Item, T> newItem(
            String itemId,
            Function<Item.Properties, T> constructor,
            UnaryOperator<Item.Properties> propertyModifier
    ) {
        if (!usedBuilderRegistry) {
            throw new IllegalArgumentException("Cannot register items, Use ItemsBuilder.createRegistry() instead of DeferredRegister.create()!");
        }
        return registry.register(itemId, () -> {
            Item.Properties props = propertyModifier.apply(new Item.Properties());
            return constructor.apply(props);
        });
    }


    // Overload for simple items that don't need custom properties
    public static <T extends Item> DeferredHolder<Item, T> newItem(String itemId, Function<Item.Properties, T> constructor) {
        return newItem(itemId, constructor, p -> p);
    }
}