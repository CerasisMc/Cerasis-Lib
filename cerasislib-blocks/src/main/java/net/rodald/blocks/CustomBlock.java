package net.rodald.blocks;

import net.rodald.blocks.interfaces.DirectionalBlock;
import net.rodald.cerasislib.items.CustomItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public abstract class CustomBlock extends CustomItem {
    public static final Material DEFAULT_MATERIAL = Material.ECHO_SHARD;

    @Override
    public @NotNull Material getMaterial() {
        return DEFAULT_MATERIAL;
    }

    public abstract Material getBlockType();

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
        world.getBlockAt(location).setType(this.getBlockType());

        location.getWorld()
                .spawn(location.clone().add(.5f, .5f, .5f), ItemDisplay.class, itemDisplay -> {
                    itemDisplay.setItemStack(this.createItem());
                    if (this instanceof DirectionalBlock) {
                        BlockFace blockFace = player.getFacing();

                        itemDisplay.getPersistentDataContainer().set(
                                new NamespacedKey("cerasis", "facing"),
                                PersistentDataType.STRING,
                                blockFace.name()
                        );

                        itemDisplay.setTransformation(new Transformation(
                                new Vector3f(0, 0, 0),
                                new Quaternionf().rotateY((float) Math.toRadians(blockFaceToYaw(blockFace))),
                                new Vector3f(1, 1, 1),
                                new Quaternionf()
                        ));
                    }
                });
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
