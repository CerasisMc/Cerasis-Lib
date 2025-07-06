package net.rodald.cerasislib.items.interfaces;

import net.rodald.cerasislib.items.CustomItem;
import org.bukkit.inventory.ItemStack;

/**
 * This interface is used by other interfaces and should not be implemented directly into an item.
 * <p>
 * Use the {@link CustomItem#prepareItem(ItemStack)} method instead.
 */
public interface PrepareInterface {
    void prepareItem(ItemStack itemStack);
}
