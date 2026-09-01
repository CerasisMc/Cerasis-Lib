package net.rodald.cerasislib.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.rodald.cerasislib.item.interfaces.PrepareInterface;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractItem {

    public static final Map<ItemStack, AbstractItem> abstractItems = new HashMap<>();

    public abstract @NotNull Material getMaterial();

    public abstract Component getItemName();

    public abstract List<Component> getItemLore();

    public AbstractItem() {
        abstractItems.put(this.createItem(), this);
    }

    /**
     * Returns the {@link NamespacedKey} used for identifying custom textures.
     * <p>
     * This can be used to reference resource pack assets such as custom GUI icons.
     * <p>
     * <strong>Example usage:</strong>
     * <pre>{@code
     * @Override
     * public NamespacedKey getNamespacedKey() {
     *     return new NamespacedKey("cerasis", "lobby/settings_icon");
     * }
     * }</pre>
     *
     * @return the {@link NamespacedKey} pointing to the custom texture. {@code null} if no custom texture is used.
     */
    public abstract NamespacedKey getNamespacedKey();

    /**
     * Optional method to prepare itemStack-specific customizations.
     * Can be overridden by subclasses to apply additional meta-settings.
     *
     * @param itemStack The itemStack stack to prepare.
     */
    protected void prepareItem(ItemStack itemStack) {
    }

    public void setItem(Player player, int slot) {
        ItemStack item = createItem();
        player.getInventory().setItem(slot, item);
    }

    public void setItem(Player player) {
        ItemStack item = createItem();
        player.getInventory().addItem(item);
    }

    /**
     * Retrieves or reconstructs a CustomItem from an ItemStack.
     *
     * @param itemStack The ItemStack to check and retrieve a CustomItem for.
     * @return The corresponding CustomItem, or null if invalid or reconstruction failed.
     */
    public static AbstractItem getAbstractItem(ItemStack itemStack) {
        if (!isAbstractItem(itemStack)) {
            return null;
        }

        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        // Get the class name from PersistentDataContainer
        String className = container.get(new NamespacedKey("cerasis", "custom_item_key"), PersistentDataType.STRING);

        if (className == null) {
            return null;
        }

        // Check if the CustomItem is already registered
        for (AbstractItem abstractItem : abstractItems.values()) {
            if (abstractItem.getClass().getName().equals(className)) {
                return abstractItem;
            }
        }

        // Try to create a new instance of the matching class
        Bukkit.getLogger().info("Try to create Item...");
        Bukkit.getLogger().info("Class: " + className);
        try {
            Class<?> clazz = Class.forName(className);
            if (AbstractItem.class.isAssignableFrom(clazz)) {
                Constructor<?> constructor = clazz.getConstructor();
                return (AbstractItem) constructor.newInstance();
            } else {
                throw new IllegalArgumentException("Class " + className + " is not a subclass of CustomItem.");
            }
        } catch (Exception e) {
            Bukkit.getLogger().severe("Failed to create CustomItem for class: " + className);
            e.printStackTrace();
            return null;
        }
    }


    public ItemStack createItem() {
        ItemStack item = new ItemStack(this.getMaterial());
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.displayName(getItemName());
            List<Component> modifiedLore = getItemLore().stream()
                    .map(component -> component.decoration(TextDecoration.ITALIC, false))
                    .toList();

            meta.lore(modifiedLore);
            meta.setItemModel(getNamespacedKey());

            meta.getPersistentDataContainer().set(
                    new NamespacedKey("cerasis", "custom_item_key"),
                    PersistentDataType.STRING,
                    this.getClass().getName()
            );
            item.setItemMeta(meta);
        }

        if (this instanceof PrepareInterface prepareInterface) {
            prepareInterface.prepareItem(item);
        }
        prepareItem(item);

        return item;
    }

    /**
     * Checks whether the given item stack is a custom item by searching for the `custom_item_key`.
     *
     * @param itemStack The item stack to check.
     * @return True if it's a custom item, false otherwise.
     */
    public static boolean isAbstractItem(ItemStack itemStack) {
        if (itemStack == null || !itemStack.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        return container.has(new NamespacedKey("cerasis", "custom_item_key"), PersistentDataType.STRING);
    }


    protected Component applyGradient(String text, TextColor startColor, TextColor endColor) {
        Component gradientBuilder = Component.empty();
        int textLength = text.length();

        for (int i = 0; i < textLength; i++) {
            double ratio = (double) i / (textLength - 1);
            TextColor currentColor = interpolateColor(startColor, endColor, ratio);
            gradientBuilder = gradientBuilder.append(Component.text(String.valueOf(text.charAt(i)), currentColor));
        }

        return gradientBuilder.decoration(TextDecoration.ITALIC, false);
    }


    private TextColor interpolateColor(TextColor start, TextColor end, double ratio) {
        int startRed = start.red();
        int startGreen = start.green();
        int startBlue = start.blue();

        int endRed = end.red();
        int endGreen = end.green();
        int endBlue = end.blue();

        int red = (int) (startRed + (endRed - startRed) * ratio);
        int green = (int) (startGreen + (endGreen - startGreen) * ratio);
        int blue = (int) (startBlue + (endBlue - startBlue) * ratio);

        return TextColor.color(red, green, blue);
    }

    public static class Builder {
        private Component displayName = Component.empty();
        private List<Component> lore = new ArrayList<>();
        private Material material = Material.AIR;
        private NamespacedKey namespacedKey;

        public AbstractItem.Builder displayName(Component text) {
            this.displayName = text;
            return this;
        }

        public AbstractItem.Builder lore(List<Component> lore) {
            this.lore = lore;
            return this;
        }

        public AbstractItem.Builder material(Material material) {
            this.material = material;
            return this;
        }

        public AbstractItem.Builder namespacedKey(NamespacedKey namespacedKey) {
            this.namespacedKey = namespacedKey;
            return this;
        }

        public AbstractItem build() {
            return new AbstractItem() {
                @Override
                public @NotNull Material getMaterial() {
                    return material;
                }

                @Override
                public Component getItemName() {
                    return displayName;
                }

                @Override
                public List<Component> getItemLore() {
                    return lore;
                }

                @Override
                public NamespacedKey getNamespacedKey() {
                    return namespacedKey;
                }
            };
        }
    }
}