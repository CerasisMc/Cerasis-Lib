package net.rodald.cerasislib.items.interfaces;

import org.bukkit.event.entity.EntityDamageByEntityEvent;

public interface EntityDamageByEntity {

    /**
     * Called when an entity is damaged by another entity.
     *
     * @param event The EntityDamageByEntityEvent
     */
    void handleEntityDamageByEntity(EntityDamageByEntityEvent event);
}
