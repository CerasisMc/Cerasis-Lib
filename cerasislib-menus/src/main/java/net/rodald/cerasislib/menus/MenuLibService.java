package net.rodald.cerasislib.menus;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class MenuLibService implements Listener {

    private static JavaPlugin instance;

    private static final HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();

    /**
     * Initializes the item library service and registers event handlers.
     *
     * @param plugin The JavaPlugin instance to register events with
     */
    public static void init(JavaPlugin plugin) {
        instance = plugin;
        MenuLibService service = new MenuLibService();
        plugin.getServer().getPluginManager().registerEvents(service, plugin);
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent event){
        InventoryHolder holder = event.getInventory().getHolder();
        //If the inventoryholder of the inventory clicked on
        // is an instance of Menu, then gg. The reason that
        // an InventoryHolder can be a Menu is because our Menu
        // class implements InventoryHolder!!
        if (holder instanceof Menu menu) {
            event.setCancelled(true); //prevent them from fucking with the inventory
            if (event.getCurrentItem() == null) { //deal with null exceptions
                return;
            }
            //Since we know our inventoryholder is a menu, get the Menu Object representing
            // the menu we clicked on
            //Call the handleMenu object which takes the event and processes it
            menu.handleMenu(event);
        }
    }

    public static PlayerMenuUtility getPlayerMenuUtility(Player player) {
        if (!(playerMenuUtilityMap.containsKey(player))) {

            PlayerMenuUtility playerMenuUtility = new PlayerMenuUtility(player);
            playerMenuUtilityMap.put(player, playerMenuUtility);
            return playerMenuUtility;
        } else {
            return playerMenuUtilityMap.get(player);
        }
    }
}