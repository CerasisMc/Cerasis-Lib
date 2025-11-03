package net.rodald.cerasislib.items.interfaces;

import net.rodald.cerasislib.items.CustomItem;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * This interface is for implementing interactable such as right and left-click logic to CustomItems.
 */
public interface Interactable {

    /**
     * Called when a player left-clicks a {@link CustomItem}.
     *
     * @param event The PlayerInteractEvent
     */
    void handleLeftClick(PlayerInteractEvent event);

    /**
     * Called when a player right-clicks a {@link CustomItem}.
     *
     * @param event The PlayerInteractEvent
     */
    void handleRightClick(PlayerInteractEvent event);

    /**
     * @return Weather or not, the item should be consumed when used.
     */
    boolean clearItemOnUse();

    /**
     * If the item should handle dropping the same as right/left-clicking it.
     * @return true if the item should handle dropping, false otherwise.
     * Default {@code false}.
     */
    default boolean activateOnDrop() {
        return false;
    }

    // handle player right and left-clicking
    default void handleItemAction(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (clearItemOnUse()) {
            int amount = item.getAmount() - 1;
            item.setAmount(amount);
            player.getInventory().setItemInMainHand(amount > 0 ? item : null);
        }

        if (event.getAction().isLeftClick()) {
            handleLeftClick(event);
        } else if (event.getAction().isRightClick()) {
            handleRightClick(event);
        }
    }

    // Executes if the player drops the item
    default void handleItemAction(PlayerDropItemEvent event) {
        // only execute if the item should handle dropping
        if (!activateOnDrop()) return;

        Player player = event.getPlayer();
        ItemStack item = event.getItemDrop().getItemStack();
        int playerBlockInteractionRange = (int) player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE).getBaseValue();
        Block targetBlock = player.getTargetBlockExact(playerBlockInteractionRange);
        BlockFace targetFace = player.getTargetBlockFace(playerBlockInteractionRange);

        if (targetFace == null) {
            targetFace = BlockFace.SELF;
        }

        PlayerInteractEvent playerInteractEvent = new PlayerInteractEvent(player, Action.RIGHT_CLICK_AIR, item, targetBlock, targetFace);
        this.handleItemAction(playerInteractEvent);
    }
}
