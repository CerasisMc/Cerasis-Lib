package net.rodald.director;

import org.bukkit.plugin.java.JavaPlugin;

public class DirectorLibService {
    private static JavaPlugin instance;

    public static void init(JavaPlugin plugin) {
        instance = plugin;
    }

    public static JavaPlugin getInstance() {
        return instance;
    }
}
