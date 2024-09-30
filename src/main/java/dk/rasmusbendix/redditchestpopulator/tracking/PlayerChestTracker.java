package dk.rasmusbendix.redditchestpopulator.tracking;

import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerChestTracker extends PersistentChestTrackerSet implements Listener {

    public PlayerChestTracker(JavaPlugin plugin) {
        super("player-placed-chests", plugin);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerPlaceBlock(BlockPlaceEvent e) {
        if(e.getBlockPlaced().getState() instanceof Chest chest) {
            chestLocations.add(chest.getLocation());
        }
    }

}
