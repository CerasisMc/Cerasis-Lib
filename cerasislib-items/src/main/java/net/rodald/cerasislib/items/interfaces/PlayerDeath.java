package net.rodald.cerasislib.items.interfaces;

import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * This interface is for implementing death logic to CustomItems.
 */
public interface PlayerDeath {

    /**
     * Called when a Player dies with a CustomItem in his mainHand. Useful for resetting the item.
     *
     * @param event The PlayerDeathEvent
     */
    void handlePlayerDeath(PlayerDeathEvent event);
}
