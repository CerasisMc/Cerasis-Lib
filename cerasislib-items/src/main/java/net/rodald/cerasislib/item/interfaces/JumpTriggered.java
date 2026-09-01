package net.rodald.cerasislib.item.interfaces;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import net.rodald.cerasislib.item.AbstractItem;

/**
 * Allows {@link AbstractItem} to react to Players jumping.
 */
public interface JumpTriggered {

    /**
     * Called when a player jumps with an {@link AbstractItem} in their mainHand.
     *
     * @param event The PlayerJumpEvent
     */
    void handlePlayerJump(PlayerJumpEvent event);
}
