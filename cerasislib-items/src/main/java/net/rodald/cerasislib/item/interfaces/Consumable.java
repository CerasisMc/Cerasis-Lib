package net.rodald.cerasislib.item.interfaces;

import net.rodald.cerasislib.item.AbstractItem;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.FoodComponent;

/**
 * This interface is for implementing consumable logic to CustomItems.
 * <p>
 * Note: For the Item to be consumable, {@link AbstractItem#getMaterial()} must return a food material.}
 */
public non-sealed interface Consumable extends PrepareInterface {
    void handleConsumption(PlayerItemConsumeEvent event);

    @Override
    default void prepareItem(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        FoodComponent foodComponent = itemMeta.getFood();

        foodComponent.setSaturation(getNutrition());
        foodComponent.setSaturation(getSaturation());
        foodComponent.setCanAlwaysEat(canAlwaysEat());

        itemMeta.setFood(foodComponent);
        itemStack.setItemMeta(itemMeta);
    }

    int getNutrition();

    float getSaturation();

    boolean canAlwaysEat();
}
