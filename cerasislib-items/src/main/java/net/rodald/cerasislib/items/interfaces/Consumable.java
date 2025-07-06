package net.rodald.cerasislib.items.interfaces;

import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface Consumable extends FoodComponent, PrepareInterface {
    void handleConsumption(PlayerItemConsumeEvent event);

    @Override
    default void setNutrition(int i) {

    }

    @Override
    default void setSaturation(float v) {

    }

    @Override
    default void setCanAlwaysEat(boolean b) {

    }

    @Override
    default void prepareItem(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setFood(this);
        itemStack.setItemMeta(itemMeta);
    }

    @Override
    @NotNull default Map<String, Object> serialize() {
        return Map.of();
    }
}
