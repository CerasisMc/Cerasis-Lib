package net.rodald.display;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class DisplayLibService implements Listener {

    private static JavaPlugin instance;

    /**
     * Initializes the item library service and registers event handlers.
     *
     * @param plugin The JavaPlugin instance to register events with
     */
    public static void init(JavaPlugin plugin) {
        instance = plugin;
    }

    public static JavaPlugin getInstance() {
        return instance;
    }
}