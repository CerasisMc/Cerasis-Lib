package net.rodald.cerasislib.item.interfaces;

import net.rodald.cerasislib.item.AbstractItem;
import org.bukkit.inventory.ItemStack;

/**
 * This interface is used by other interfaces and should not be implemented directly into an item.
 * <p>
 * Use the {@link AbstractItem#prepareItem(ItemStack)} method instead.
 */
public sealed interface PrepareInterface permits Consumable {
    void prepareItem(ItemStack itemStack);
}
