package net.rodald.blocks;

import net.rodald.blocks.interfaces.Touchable;
import net.rodald.cerasislib.items.CustomItem;
import net.rodald.cerasislib.items.interfaces.Tickable;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;

import java.util.Collection;
import java.util.Map;

public class BlockLibService implements Listener {

    private static JavaPlugin instance;

    /**
     * Initializes the item library service and registers event handlers.
     *
     * @param plugin The JavaPlugin instance to register events with
     */
    public static void init(JavaPlugin plugin) {
        instance = plugin;
        BlockLibService service = new BlockLibService();
        plugin.getServer().getPluginManager().registerEvents(service, plugin);

        onTick();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        handleBlockBreak(event.getBlock(), event.getPlayer().getGameMode(), event);
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        handleBlockBreak(event.getBlock(), null, null);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        for (Block block : event.blockList()) {
            handleBlockBreak(block, null, null);
        }
    }

    private void handleBlockBreak(Block block, GameMode gameMode, BlockBreakEvent event) {
        Location blockLocation = block.getLocation().add(0.5, 0.5, 0.5);

        for (Entity entity : block.getChunk().getEntities()) {
            if (entity instanceof ItemDisplay itemDisplay) {
                Location itemDisplayLocation = itemDisplay.getLocation();

                itemDisplayLocation.setYaw(blockLocation.getYaw());
                itemDisplayLocation.setPitch(blockLocation.getPitch());

                if (blockLocation.equals(itemDisplayLocation)) {
                    ItemStack displayedItem = itemDisplay.getItemStack();

                    if (gameMode == GameMode.SURVIVAL && event.isDropItems()) {

                        // Drop the custom item
                        block.getWorld().dropItem(blockLocation, displayedItem);
                        event.setDropItems(false);
                    }

                    Bukkit.broadcastMessage("BLOCK BREAK");
                    itemDisplay.remove();
                    block.setType(Material.AIR);
                }
            }
        }
    }

    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem();

        CustomItem customItem = CustomItem.getCustomItem(itemStack);

        if (customItem == null) return;

        if (customItem instanceof CustomBlock customBlock) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null) {
                Block clickedBlock = event.getClickedBlock();
                BlockFace face = event.getBlockFace();
                // new block pos
                Location placeLocation = clickedBlock.getRelative(face).getLocation();

                Block targetBlock = placeLocation.getBlock();
                if (targetBlock.isReplaceable()) {

                    // bounding box tests
                    BoundingBox blockBox = BoundingBox.of(targetBlock);
                    boolean collision = !targetBlock.getWorld()
                            .getNearbyEntities(blockBox)
                            .stream()
                            .filter(entity -> !(entity instanceof ArmorStand))
                            .toList()
                            .isEmpty();

                    if (collision) return;


                    // place custom block: use scheduler so block doesnt get placed twice
                    Bukkit.getScheduler().runTask(instance, () -> {
                        customBlock.place(targetBlock.getWorld(), placeLocation, player);
                        player.swingHand(event.getHand());
                    });

                    // remove item if player is not in creative mode
                    if (!event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
                        itemStack.setAmount(itemStack.getAmount() - 1);
                    }

                    if (player.isSneaking()) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    private static void onTick() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (World world : Bukkit.getWorlds()) {
                    for (ItemDisplay itemDisplay : world.getEntitiesByClass(ItemDisplay.class)) {
                        if (!CustomBlock.isCustomBlock(itemDisplay)) continue;

                        CustomBlock customBlock = CustomBlock.getCustomBlock(itemDisplay);
                        if (!(customBlock instanceof Touchable touchable)) continue;
                        Block block = itemDisplay.getLocation().getBlock();
                        Location blockLocation = block.getLocation().add(0.5, 0.5, 0.5);

                        for (Entity entity : world.getNearbyEntities(blockLocation, 0.5, 0.5, 0.5)) {
                            if (!entity.isTicking()) continue;

                            switch (entity.getType()) {
                                case ITEM_DISPLAY, TEXT_DISPLAY, BLOCK_DISPLAY,
                                     ITEM_FRAME, GLOW_ITEM_FRAME, ARMOR_STAND -> {
                                    continue;
                                }
                            }

                            Collection<BoundingBox> collisionShapes = block.getCollisionShape().getBoundingBoxes();
                            // for some reason collision shapes can be empty if the block has a "primitive shape" (such as slabs)
                            if (collisionShapes.isEmpty()) {
                                if (isStandingOn(block.getBoundingBox(), entity.getBoundingBox())) {
                                    touchable.handleSteppedOn(blockLocation, entity, itemDisplay);
                                }
                            } else {
                                for (BoundingBox collisionShape : collisionShapes) {
                                    collisionShape.shift(block.getX(), block.getY(), block.getZ());
                                    if (isStandingOn(collisionShape, entity.getBoundingBox())) {
                                        touchable.handleSteppedOn(blockLocation, entity, itemDisplay);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(instance, 0L, 1L);
    }


    public static boolean isStandingOn(BoundingBox blockBox, BoundingBox playerBox) {
        // if the maxY of the block bounding box == the minY of the players box, if they intersect 2d they are standing on it
        // unfortunately bukkit's BoundingBox doesn't have an intersect2d method, so we shift it up.
        return blockBox.getMaxY() == playerBox.getMinY();
    }

    public static JavaPlugin getInstance() {
        return instance;
    }
}