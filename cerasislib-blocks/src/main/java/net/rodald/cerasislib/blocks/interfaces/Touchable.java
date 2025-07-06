package net.rodald.cerasislib.blocks.interfaces;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;

public interface Touchable {
    void handleSteppedOn(Location location, Entity entity, ItemDisplay itemDisplay);
}
