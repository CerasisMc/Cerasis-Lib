package net.rodald.cerasislib.items.interfaces;

import org.bukkit.event.player.PlayerItemConsumeEvent;

public interface Consumable {
    void handleConsumption(PlayerItemConsumeEvent event);
}
