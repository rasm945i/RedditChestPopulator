package dk.rasmusbendix.redditchestpopulator.tracking;

import dk.rasmusbendix.redditchestpopulator.CustomConfig;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public abstract class PersistentChestTrackerSet implements ChestTracker {

    protected Set<Location> chestLocations;
    protected CustomConfig config;
    protected JavaPlugin plugin;

    public PersistentChestTrackerSet(String name, JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = new CustomConfig(name, "", plugin);
        this.chestLocations = getChestLocation(config);
    }

    @Override
    public boolean canRestockChest(Chest chest) {
        return !chestLocations.contains(chest.getLocation());
    }

    @SuppressWarnings("DataFlowIssue")
    private Set<Location> getChestLocation(CustomConfig config) {

        Set<Location> locations = new HashSet<>();

        if (!config.contains("ignored-locations")) {
            plugin.getLogger().info("No ignored chests found in config!");
            return locations;
        }

        for (String key : config.getConfig().getConfigurationSection("ignored-locations").getKeys(false)) {
            locations.add(config.getConfig().getLocation("ignored-locations." + key));
        }

        plugin.getLogger().info(chestLocations.size() + " ignored locations was loaded.");
        return locations;

    }

    public void save() {
        int i = 0;
        for (Location loc : chestLocations) {
            config.set("ignored-chests." + i, loc);
            i++;
        }
        config.saveConfig();
    }

}
