package dk.rasmusbendix.redditchestpopulator.chunk;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dk.rasmusbendix.redditchestpopulator.ChestPopulatorPlugin;
import dk.rasmusbendix.redditchestpopulator.CustomConfig;
import dk.rasmusbendix.redditchestpopulator.chest.ChestTier;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.world.ChunkLoadEvent;

import java.util.*;

/**
 * If a player wants to have their player-placed chests become "world generated chests", they simply have to delete them
 *  from the player-placed-chests.yml file. This way they will get restocked once, and then marked as a world-chest
 * TODO: Allow players to make themselves as "builders" or similar, so chests they place wont get flagged as player-placed
 * */

public class ChunkEvents implements Listener {

    private final ChestPopulatorPlugin plugin;
    private final CustomConfig chestConfig;
    private final CustomConfig playerConfig;
    private final List<Location> chests;
    private Set<Location> chestLocations;
    private Set<Location> playerChestLocations;
    private int currentIndex = 0;

    public ChunkEvents(ChestPopulatorPlugin plugin) {
        this.plugin = plugin;
        this.chests = new ArrayList<>();
        this.chestConfig = new CustomConfig("world-generated-chests");
        this.playerConfig = new CustomConfig("player-placed-chests");
        this.chestLocations = new HashSet<>();
        this.playerChestLocations = new HashSet<>();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        loadIgnoredChests();
    }

    private void loadIgnoredChests() {
        playerChestLocations = loadChestLocation(playerConfig);
        chestLocations = loadChestLocation(chestConfig);
    }

    @SuppressWarnings("DataFlowIssue")
    private Set<Location> loadChestLocation(CustomConfig config) {

        Set<Location> locations = new HashSet<>();

        if (!config.contains("ignored-chests")) {
            plugin.getLogger().info("No ignored chests found in config!");
            return locations;
        }

        for (String key : config.getConfig().getConfigurationSection("ignored-chests").getKeys(false)) {
            locations.add(config.getConfig().getLocation("ignored-chests." + key));
        }

        plugin.getLogger().info(chestLocations.size() + " ignored chests was loaded.");
        return locations;

    }

    public void saveIgnoredChests() {
        saveChestLocations(chestConfig, chestLocations);
        saveChestLocations(playerConfig, playerChestLocations);
    }

    private void saveChestLocations(CustomConfig config, Set<Location> locations) {
        int i = 0;
        for (Location loc : locations) {
            config.set("ignored-chests." + i, loc);
            i++;
        }
        config.saveConfig();
    }

    @EventHandler
    public void playerPlaceChest(BlockPlaceEvent e) {
        if (e.getBlockPlaced().getState() instanceof Chest chest) {
            playerChestLocations.add(chest.getLocation());
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        if (!e.getChunk().isLoaded()) {
            plugin.getLogger().warning("Chunk is not loaded?!");
            return;
        }

        if (!plugin.getConfig().getBoolean("settings.populate-chests", false)) {
            return;
        }

        int count = 0;
        for (BlockState state : e.getChunk().getTileEntities()) {
            if (state instanceof Chest chest) {

                if (!canBeRestocked(chest.getLocation())) {
                    logIfMuchLogging("Skipped a chest!");
                    continue;
                }

                if (plugin.getConfig().getBoolean("settings.clear-chests-before-restock")) {
                    chest.getInventory().clear();
                }
                ChestTier tier = getChestTier(chest.getLocation());
                tier.fillChest(chest, tier.generateLootTable());
                count++;
                chests.add(chest.getLocation());
                chestLocations.add(chest.getLocation());
            }
        }

        if (count > 0) {
            logIfMuchLogging("Populated " + count + " chests!");
        }

    }

    public Location nextChestLocation() {
        Location loc = chests.get(currentIndex);
        currentIndex++;
        return loc;
    }

    private ChestTier getChestTier(Location location) {
        RegionManager manager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(location.getWorld()));
        ChestTier tier = plugin.getTierManager().getDefault();

        if (manager == null) {
            plugin.getLogger().warning("Failed to find region manager for world " + location.getWorld());
            return tier;
        }

        ApplicableRegionSet ars = manager.getApplicableRegions(BlockVector3.at(location.getX(), location.getY(), location.getZ()));


        for (ProtectedRegion region : ars.getRegions()) {
            Optional<ChestTier> rgt = plugin.getTierManager().getTierForRegion(region.getId());
            if (rgt.isPresent()) {
                tier = rgt.get();
            }
        }

        return tier;

    }

    public boolean canBeRestocked(Location location) {
        return !playerChestLocations.contains(location) && !chestLocations.contains(location);
    }

    private void logIfMuchLogging(String message) {
        if(plugin.getConfig().getBoolean("settings.much-logging")) {
            plugin.getLogger().info(message);
        }
    }

}
