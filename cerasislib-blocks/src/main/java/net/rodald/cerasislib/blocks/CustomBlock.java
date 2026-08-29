package net.rodald.cerasislib.blocks;

import net.rodald.cerasislib.blocks.interfaces.Directional;
import net.rodald.cerasislib.items.CustomItem;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public abstract class CustomBlock extends CustomItem {
    public static final Material DEFAULT_MATERIAL = Material.ECHO_SHARD;

    @Override
    public @NotNull Material getMaterial() {
        return DEFAULT_MATERIAL;
    }

    /**
     * @return The material to define the custom blocks bounding box
     * <p>
     * The material must have a block variant.
     */
    public abstract @NotNull Material getBoundingBoxBlock();

    /**
     * Returns the custom hardness value of this block, measured in vanilla hardness units.
     * <p>
     * <b>Important:</b> Avoid setting values near zero (e.g., 0.0 or 0.001) to achieve instant mining.
     * The high block break speed multiplier can trigger an engine desync, causing players
     * to instamine the block behind it.
     * </p>
     * <p>
     * <b>Recommendation:</b> For instant-mineable blocks, configure the bounding box {@link #getBoundingBoxBlock()}
     * to use a native vanilla block that is already instantly breakable.
     * </p>
     *
     * @return the custom hardness value of this block
     */
    public abstract float getHardness();

    /**
     * Used for generating the block particles when mining the block
     *
     * @return The Material of the block which particle should be generated as a {@link Particle#BLOCK_CRUMBLE}.
     * <p>
     * The material must have a block variant.
     */
    public abstract @NotNull Material getParticleBlockType();

    /**
     * @return if the block can be placed inside entities
     */
    public abstract boolean isCollidable();

    @Override
    protected void prepareItem(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();

        meta.getPersistentDataContainer().set(
                new NamespacedKey("cerasis", "custom_block_key"),
                PersistentDataType.STRING,
                this.getClass().getName()
        );

        itemStack.setItemMeta(meta);
    }

    public void place(World world, Location location, Player player) {
        world.getBlockAt(location).setType(this.getBoundingBoxBlock());

        location.getWorld()
                .spawn(location.clone().add(.5f, .5f, .5f), ItemDisplay.class, itemDisplay -> {
                    itemDisplay.setItemStack(this.createItem());
                    if (this instanceof Directional) {
                        BlockFace blockFace = player.getFacing();

                        itemDisplay.getPersistentDataContainer().set(
                                new NamespacedKey("cerasis", "facing"),
                                PersistentDataType.STRING,
                                blockFace.name()
                        );

                        itemDisplay.setViewRange((1 + Bukkit.getViewDistance()) * 16);

                        itemDisplay.setTransformation(new Transformation(
                                new Vector3f(0, 0, 0),
                                new Quaternionf().rotateY((float) Math.toRadians(blockFaceToYaw(blockFace))),
                                new Vector3f(1, 1, 1),
                                new Quaternionf()
                        ));
                    }
                });
        world.playSound(location, "block." + this.getParticleBlockType().getKey().getKey() + ".place", 1, 1);
    }


    public static CustomBlock getCustomBlock(@Nullable Block block) {
        if (block == null) return null;

        Location blockLocation = block.getLocation().add(0.5, 0.5, 0.5);

        for (Entity entity : block.getChunk().getEntities()) {
            if (entity instanceof ItemDisplay itemDisplay) {
                Location itemDisplayLocation = itemDisplay.getLocation();

                itemDisplayLocation.setYaw(blockLocation.getYaw());
                itemDisplayLocation.setPitch(blockLocation.getPitch());

                if (blockLocation.equals(itemDisplayLocation) && CustomBlock.isCustomBlock(itemDisplay)) {
                    return CustomBlock.getCustomBlock(itemDisplay);
                }
            }
        }

        return null;
    }

    public static boolean isCustomBlock(ItemDisplay itemDisplay) {
        return isCustomItem(itemDisplay.getItemStack());
    }

    public static CustomBlock getCustomBlock(ItemDisplay itemDisplay) {
        CustomItem customItem = CustomItem.getCustomItem(itemDisplay.getItemStack());

        if (customItem instanceof CustomBlock customBlock) {
            return customBlock;
        }

        return null;
    }

    private float blockFaceToYaw(BlockFace blockFace) {
        return switch (blockFace) {
            case SOUTH -> 0;
            case EAST -> 90;
            case NORTH -> 180;
            case WEST -> -90;
            default -> -1;
        };
    }
}
