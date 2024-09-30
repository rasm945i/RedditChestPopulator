package dk.rasmusbendix.redditchestpopulator.tracking;

import dk.rasmusbendix.redditchestpopulator.restock.ChestRestockEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class RestockTracker extends PersistentChestTrackerSet implements Listener {

    public RestockTracker(JavaPlugin plugin) {
        super("world-generated-chests", plugin);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChestRestock(ChestRestockEvent restockEvent) {
        if(restockEvent.isCancelled())
            return;
        chestLocations.add(restockEvent.getChest().getLocation());
    }

}
