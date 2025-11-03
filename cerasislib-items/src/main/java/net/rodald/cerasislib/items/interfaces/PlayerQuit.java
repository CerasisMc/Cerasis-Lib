package net.rodald.cerasislib.items.interfaces;

import org.bukkit.event.player.PlayerQuitEvent;

/**
 * This interface is for implementing quit logic to CustomItems.
 */
public interface PlayerQuit {
    /**
     * Called when a player quits the server with a CustomItem in his inventory.
     *
     * @param event The PlayerQuitEvent
     */
    void handlePlayerQuitInventory(PlayerQuitEvent event);

    /**
     * Called when a player quits the server with a CustomItem in his main hand.
     *
     * @param event The PlayerQuitEvent
     */
    void handlePlayerQuitMainHand(PlayerQuitEvent event);
}
