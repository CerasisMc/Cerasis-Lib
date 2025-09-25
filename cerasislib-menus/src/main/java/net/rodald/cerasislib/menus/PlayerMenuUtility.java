package net.rodald.cerasislib.menus;

import org.bukkit.entity.Player;

/**
 * Companion class to all menus.
 * This is needed to pass information across the entire
 * menu system no matter how many inventories are opened or closed.
 * <p>
 * Each player has one of these objects, and only one.
 * This class maintains player-specific data and state for menu navigation,
 * ensuring that menu interactions remain consistent and contextual for each player.
 */

public class PlayerMenuUtility {

    private final Player owner;

    /**
     * Creates a new PlayerMenuUtility instance for the specified player.
     *
     * @param player The player who will own this menu utility instance
     */
    public PlayerMenuUtility(Player player) {
        this.owner = player;
    }

    /**
     * Gets the player associated with this menu utility.
     *
     * @return The player who owns this menu utility instance
     */
    public Player getOwner() {
        return owner;
    }

}
