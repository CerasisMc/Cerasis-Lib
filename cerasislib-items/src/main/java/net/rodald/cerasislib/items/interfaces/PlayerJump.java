package net.rodald.cerasislib.items.interfaces;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;

/**
 * This interface is for implementing jump logic to CustomItems.
 */
public interface PlayerJump {

    /**
     * Called when a player jumps with a CustomItem in his mainHand.
     *
     * @param event The PlayerJumpEvent
     */
    void handlePlayerJump(PlayerJumpEvent event);
}
