package net.rodald.cerasislib.items;

import net.kyori.adventure.text.Component;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class UsableItem extends CustomItem {
    private static final Map<Component, UsableItem> usableItems = new HashMap<>();


    public UsableItem() {
        usableItems.put(getItemName(), this);
    }

    public boolean clearItemOnUse() { return false; };
    public abstract void handleRightClick(PlayerInteractEvent e);

    public abstract void spawnParticles(Player p);
    public abstract void playSound(Player p);
    
    // Methode zum Behandeln der Item-Action
    public void handleItemAction(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        ItemStack item = e.getItem();

        if (clearItemOnUse()) {
            int amount = item.getAmount() - 1;
            item.setAmount(amount);
            p.getInventory().setItemInMainHand(amount > 0 ? item : null);
        }

        handleRightClick(e);

        spawnParticles(p);
        playSound(p);
    }

    // Executes if the player drops the item
    public void handleItemAction(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItemDrop().getItemStack();
        int playerBlockInteractionRange = (int) Objects.requireNonNull(player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE)).getBaseValue();
        Block targetBlock = player.getTargetBlockExact(playerBlockInteractionRange);
        BlockFace targetFace = player.getTargetBlockFace(playerBlockInteractionRange);

        PlayerInteractEvent playerInteractEvent = new PlayerInteractEvent(player, Action.RIGHT_CLICK_AIR, item, targetBlock, targetFace);
        this.handleItemAction(playerInteractEvent);
    }


    public static UsableItem getItemByName(Component displayName) {
        return usableItems.get(displayName);
    }
}
