package net.rodald.cerasislib.menus;

import org.bukkit.entity.Player;

/**
 * Companion class to all menus.
 * This is needed to pass information across the entire
 * menu system no matter how many inventories are opened or closed.
 * <p>
 * Each player has one of these objects, and only one.
 */

public class PlayerMenuUtility {

    private final Player owner;

    public PlayerMenuUtility(Player player) {
        this.owner = player;
    }

    public Player getOwner() {
        return owner;
    }

}
