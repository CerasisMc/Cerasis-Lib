package net.rodald.cerasislib.items;

import net.kyori.adventure.text.Component;
import net.rodald.cerasislib.items.interfaces.Interactable;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemLibService implements Listener {

    /**
     * Initializes the item library service and registers event handlers.
     *
     * @param plugin The JavaPlugin instance to register events with
     */
    public static void init(JavaPlugin plugin) {
        ItemLibService service = new ItemLibService();
        plugin.getServer().getPluginManager().registerEvents(service, plugin);
    }

    @EventHandler
    private void onPlayerDropItem(PlayerDropItemEvent event) {
        ItemStack itemStack = event.getItemDrop().getItemStack();

        event.setCancelled(true);

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return;

        Component displayName = meta.displayName();
        CustomItem customItem = CustomItem.getItemByName(displayName);

        if (customItem == null) return;

        if (customItem instanceof Interactable interactable) {
            interactable.handleItemAction(event);
        }
    }

    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack itemStack = event.getItem();

        event.setCancelled(true);
        event.setUseItemInHand(Event.Result.ALLOW);

        if (itemStack == null) return;
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return;

        Component displayName = meta.displayName();
        CustomItem customItem = CustomItem.getItemByName(displayName);

        if (customItem == null) return;

        if (customItem instanceof Interactable interactable) {
            interactable.handleItemAction(event);
        }
    }
}
