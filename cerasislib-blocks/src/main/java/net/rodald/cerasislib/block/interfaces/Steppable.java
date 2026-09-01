package net.rodald.cerasislib.block.interfaces;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;

public interface Steppable {
    void handleSteppedOn(Location location, Entity entity, ItemDisplay itemDisplay);
}
