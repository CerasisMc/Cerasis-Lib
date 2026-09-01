package net.rodald.cerasislib.item.interfaces;

import org.bukkit.event.block.BlockBreakEvent;

/**
 * This interface is for implementing block-breaking logic to CustomItems.
 */
public interface BlockBreakTriggered {

    /**
     * Called when a block is broken using a CustomItem.
     *
     * @param event The BlockBreakEvent
     */
    void handleBlockBreak(BlockBreakEvent event);
}
