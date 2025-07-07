package net.rodald.cerasislib.blocks;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.EnumWrappers;
import io.papermc.paper.event.player.PlayerPickBlockEvent;
import net.rodald.cerasislib.blocks.interfaces.Touchable;
import net.rodald.cerasislib.items.CustomItem;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;

import java.util.*;

public class BlockLibService implements Listener {

    private static JavaPlugin instance;
    private static ProtocolManager protocolManager;

    private final Set<UUID> alreadyTeleported = new HashSet<>();
    private static final Map<Player, Integer> particleTasks = new HashMap<>();

    /**
     * Initializes the item library service and registers event handlers.
     *
     * @param plugin The JavaPlugin instance to register events with
     */
    public static void init(JavaPlugin plugin) {
        protocolManager = ProtocolLibrary.getProtocolManager();
        instance = plugin;
        BlockLibService service = new BlockLibService();
        plugin.getServer().getPluginManager().registerEvents(service, plugin);
        interceptPackets();

        onTick();
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent event) {
        handleBlockBreak(event.getBlock(), event.getPlayer().getGameMode(), event);
    }

    @EventHandler
    private void onBlockBurn(BlockBurnEvent event) {
        handleBlockBreak(event.getBlock(), null, null);
    }

    @EventHandler
    private void onBlockPistonExtend(BlockPistonExtendEvent event) {
        alreadyTeleported.clear();
        for (Block block : event.getBlocks()) {
            alreadyTeleported.add(handlePistonMove(block, event.getDirection(), alreadyTeleported));
        }
    }

    @EventHandler
    private void onBlockPistonRetract(BlockPistonRetractEvent event) {
        alreadyTeleported.clear();
        for (Block block : event.getBlocks()) {
            alreadyTeleported.add(handlePistonMove(block, event.getDirection(), alreadyTeleported));
        }
    }

    @EventHandler
    private void onEntityExplode(EntityExplodeEvent event) {
        for (Block block : event.blockList()) {
            handleBlockBreak(block, null, null);
        }
    }

    @EventHandler
    private void onPlayerPickBlock(PlayerPickBlockEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        Location blockLocation = block.getLocation().add(0.5, 0.5, 0.5);

        for (Entity entity : block.getChunk().getEntities()) {
            if (!(entity instanceof ItemDisplay itemDisplay)) continue;

            Location itemDisplayLocation = itemDisplay.getLocation();

            itemDisplayLocation.setYaw(blockLocation.getYaw());
            itemDisplayLocation.setPitch(blockLocation.getPitch());

            if (!blockLocation.equals(itemDisplayLocation)) continue;

            ItemStack displayedItem = itemDisplay.getItemStack();

            for (int i = 0; i < player.getInventory().getSize(); i++) {
                if (!displayedItem.isSimilar(player.getInventory().getItem(i))) continue;

                if (i <= 8) {
                    player.getInventory().setHeldItemSlot(i);
                    return;
                }

                event.setSourceSlot(i);
                return;
            }
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

    private UUID handlePistonMove(Block block, BlockFace blockFace, Set<UUID> alreadyTeleported) {
        Location blockLocation = block.getLocation().add(0.5, 0.5, 0.5);

        for (Entity entity : block.getChunk().getEntities()) {
            if (entity instanceof ItemDisplay itemDisplay) {
                UUID uuid = itemDisplay.getUniqueId();
                if (!alreadyTeleported.contains(uuid)) {
                    if (blockLocation.equals(itemDisplay.getLocation())) {
                        Location location = block.getRelative(blockFace).getLocation();
                        itemDisplay.teleport(location.add(0.5, 0.5, 0.5));
                        return uuid;
                    }
                }
            }
        }
        return null;
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
                            .filter(entity -> (entity instanceof LivingEntity))
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

    private static void interceptPackets() {
        if (protocolManager == null) return;

        protocolManager.addPacketListener(new PacketAdapter(instance, ListenerPriority.NORMAL, PacketType.Play.Client.BLOCK_DIG) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                PacketContainer packet = event.getPacket();

                EnumWrappers.PlayerDigType digType = packet.getPlayerDigTypes().read(0);
                BlockPosition blockPos = packet.getBlockPositionModifier().read(0);

                World world = player.getWorld();
                Location blockLocation = blockPos.toLocation(world).add(0.5, 0.5, 0.5);
                Block block = world.getBlockAt(blockLocation);
                for (Entity entity : block.getChunk().getEntities()) {
                    if (entity instanceof ItemDisplay itemDisplay) {
                        Location itemDisplayLocation = itemDisplay.getLocation();

                        itemDisplayLocation.setYaw(blockLocation.getYaw());
                        itemDisplayLocation.setPitch(blockLocation.getPitch());

                        if (blockLocation.equals(itemDisplayLocation) && CustomBlock.isCustomBlock(itemDisplay)) {
                            // START = Effekt starten

                            CustomBlock customBlock = CustomBlock.getCustomBlock(itemDisplay);
                            if (digType == EnumWrappers.PlayerDigType.START_DESTROY_BLOCK) {
                                if (particleTasks.containsKey(player)) return;

                                int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, () -> {
                                    spawnParticlesOnBlockFace(block, player, customBlock.getParticleBlockType());
                                }, 0L, 2L);

                                particleTasks.put(player, taskId);
                            }

                            // STOP oder ABORT = Effekt stoppen
                            if (digType == EnumWrappers.PlayerDigType.ABORT_DESTROY_BLOCK ||
                                    digType == EnumWrappers.PlayerDigType.STOP_DESTROY_BLOCK) {

                                Integer taskId = particleTasks.remove(player);
                                if (taskId != null) {
                                    Bukkit.getScheduler().cancelTask(taskId);
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    private static void spawnParticlesOnBlockFace(Block block, Player player, Material material) {
        BlockFace blockFace = player.getTargetBlockFace((int) player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE).getValue() + 1);
        World world = block.getWorld();
        BoundingBox boundingBox = block.getBoundingBox();
        Location base = boundingBox.getCenter().toLocation(world);

        double offsetX = boundingBox.getWidthX() / 4;
        double offsetY = boundingBox.getHeight() / 4;
        double offsetZ = boundingBox.getWidthZ() / 4;
        switch (blockFace) {
            case UP:
                offsetY = 0;
                base.setY(boundingBox.getMaxY() + 0.1);
                break;
            case DOWN:
                offsetY = 0;
                base.setY(boundingBox.getMinY() - 0.1);
                break;
            case NORTH:
                offsetZ = 0.0;
                base.setZ(boundingBox.getMinZ() - 0.1);
                break;
            case SOUTH:
                offsetZ = 0.0;
                base.setZ(boundingBox.getMaxZ() + 0.1);
                break;
            case WEST:
                offsetX = 0.0;
                base.setX(boundingBox.getMinX() - 0.1);
                break;
            case EAST:
                offsetX = 0.0;
                base.setX(boundingBox.getMaxX() + 0.1);
                break;
            default:
                break;
        }

        world.spawnParticle(Particle.BLOCK_CRUMBLE, base, 1, offsetX, offsetY, offsetZ, 0, material.createBlockData());
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


    private static boolean isStandingOn(BoundingBox blockBox, BoundingBox playerBox) {
        // if the maxY of the block bounding box == the minY of the players box, if they intersect 2d they are standing on it
        // unfortunately bukkit's BoundingBox doesn't have an intersect2d method, so we shift it up.
        return blockBox.getMaxY() == playerBox.getMinY();
    }

    public static JavaPlugin getInstance() {
        return instance;
    }
}