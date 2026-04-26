package M6FGR.epic_api.builders.minecraft;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;
import java.util.function.UnaryOperator;

public class ItemsBuilder {
    /**
     * Registers a new item with easy property manipulation.
     * @param itemId Unique identifier for the item.
     * @param constructor The constructor reference (e.g., MyItem::new).
     * @param propertyModifier A lambda to adjust item properties (e.g., p -> p.stacksTo(1)).
     * @param itemRegistry the DeferredRegistry in your item class.
     */
    public static <T extends Item> RegistryObject<T> newItem(
            String itemId,
            Function<Item.Properties, T> constructor,
            DeferredRegister<Item> itemRegistry,
            UnaryOperator<Item.Properties> propertyModifier
    ) {
        return itemRegistry.register(itemId, () -> {
            Item.Properties props = propertyModifier.apply(new Item.Properties());
            return constructor.apply(props);
        });
    }

    // Overload for simple items that don't need custom properties
    public static <T extends Item> RegistryObject<T> newItem(String itemId, Function<Item.Properties, T> constructor, DeferredRegister<Item> itemRegistry) {
        return newItem(itemId, constructor, itemRegistry, p -> p);
    }
}