package net.rodald.cerasislib.items;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import net.rodald.cerasislib.items.interfaces.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class ItemLibService implements Listener {

    private static JavaPlugin instance;

    /**
     * Initializes the item library service and registers event handlers.
     *
     * @param plugin The JavaPlugin instance to register events with
     */
    public static void init(JavaPlugin plugin) {
        instance = plugin;
        ItemLibService service = new ItemLibService();
        plugin.getServer().getPluginManager().registerEvents(service, plugin);
        // start onTick loop
        onTick();

    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = player.getInventory().getItemInMainHand();

        CustomItem customItem = CustomItem.getCustomItem(itemStack);

        if (customItem == null) return;

        if (customItem instanceof BlockBreak blockBreak) {
            blockBreak.handleBlockBreak(event);
        }
    }

    @EventHandler
    private void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            ItemStack itemStack = player.getInventory().getItemInMainHand();

            CustomItem customItem = CustomItem.getCustomItem(itemStack);

            if (customItem == null) return;

            if (customItem instanceof EntityDamageByEntity entityDamageByEntity) {
                entityDamageByEntity.handleEntityDamageByEntity(event);
            }
        }
    }

    @EventHandler
    private void onEntityChangeBlock(EntityChangeBlockEvent event) {
        for (Map.Entry<ItemStack, CustomItem> entry : CustomItem.customItems.entrySet()) {
            if (entry.getValue() instanceof EntityChangeBlock entityChangeBlock) {
                entityChangeBlock.handleEntityChangeBlock(event);
            }
        }
    }

    @EventHandler
    private void onPlayerDropItem(PlayerDropItemEvent event) {
        ItemStack itemStack = event.getItemDrop().getItemStack();

        CustomItem customItem = CustomItem.getCustomItem(itemStack);

        if (customItem == null) return;

        if (customItem instanceof Interactable interactable) {
            interactable.handleItemAction(event);
        }
    }

    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent event) {
        ItemStack itemStack = event.getPlayer().getInventory().getItemInMainHand();

        CustomItem customItem = CustomItem.getCustomItem(itemStack);

        if (customItem == null) return;

        if (customItem instanceof PlayerDeath playerDeath) {
            playerDeath.handlePlayerDeath(event);
        }
    }

    @EventHandler
    public void onPlayerJump(PlayerJumpEvent event) {
        ItemStack itemStack = event.getPlayer().getInventory().getItemInMainHand();

        CustomItem customItem = CustomItem.getCustomItem(itemStack);

        if (customItem == null) return;

        if (customItem instanceof PlayerJump playerJump) {
            playerJump.handlePlayerJump(event);
        }
    }


    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack itemStack = event.getItem();

        CustomItem customItem = CustomItem.getCustomItem(itemStack);

        if (customItem == null) return;

        if (customItem instanceof Interactable interactable) {
            interactable.handleItemAction(event);
        }
    }

    @EventHandler
    private void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        ItemStack itemStack = event.getItem();

        CustomItem customItem = CustomItem.getCustomItem(itemStack);

        if (customItem == null) return;

        if (customItem instanceof Consumable consumable) {
            consumable.handleConsumption(event);
        }
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        PlayerInventory playerInventory = event.getPlayer().getInventory();

        if (CustomItem.getCustomItem(playerInventory.getItemInMainHand()) instanceof PlayerQuit quit) {
            quit.handlePlayerQuitMainHand(event);
        }

        for (ItemStack item : playerInventory) {
            if (CustomItem.getCustomItem(item) instanceof PlayerQuit quit) {
                quit.handlePlayerQuitInventory(event);
                break;
            }
        }
    }

    private static void onTick() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<ItemStack, CustomItem> entry : CustomItem.customItems.entrySet()) {
                    if (entry.getValue() instanceof Tickable tickable) {
                        tickable.tick();
                    }
                }
            }
        }.runTaskTimer(instance, 0L, 1L);
    }
}