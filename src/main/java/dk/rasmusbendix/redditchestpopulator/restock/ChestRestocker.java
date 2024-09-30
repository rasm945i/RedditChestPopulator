package dk.rasmusbendix.redditchestpopulator.restock;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dk.rasmusbendix.redditchestpopulator.ChestPopulatorPlugin;
import dk.rasmusbendix.redditchestpopulator.chest.ChestTier;
import dk.rasmusbendix.redditchestpopulator.tracking.ChestTracker;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * If a player wants to have their player-placed chests become "world generated chests", they simply have to delete them
 * from the player-placed-chests.yml file. This way they will get restocked once, and then marked as a world-chest
 * TODO: Allow players to make themselves as "builders" or similar, so chests they place wont get flagged as player-placed
 */

public class ChestRestocker implements Listener {

    private final ChestPopulatorPlugin plugin;
    private final List<ChestTracker> chestTrackers;

    public ChestRestocker(ChestPopulatorPlugin plugin) {
        this.plugin = plugin;
        this.chestTrackers = new ArrayList<>();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void addTracker(ChestTracker tracker) {
        this.chestTrackers.add(tracker);
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

            if (!(state instanceof Chest chest)) {
                continue;
            }

            if (!canBeRestocked(chest)) {
                logIfMuchLogging("Skipped a chest!");
                continue;
            }

            ChestTier tier = getChestTier(chest.getLocation());
            ChestRestockEvent restockEvent = new ChestRestockEvent(chest, tier);
            plugin.getServer().getPluginManager().callEvent(restockEvent);

            if (restockEvent.isCancelled()) {
                logIfMuchLogging("Restock event was cancelled!");
                continue;
            }

            if (plugin.getConfig().getBoolean("settings.clear-chests-before-restock")) {
                chest.getInventory().clear();
            }

            restockEvent.getTier().fillChest(restockEvent.getChest(), restockEvent.getGeneratedLoot());
            count++;

        }

        if (count > 0) {
            logIfMuchLogging("Populated " + count + " chests!");
        }

    }

    private ChestTier getChestTier(Location location) {

        ChestTier tier = plugin.getTierManager().getDefault();

        if (location.getWorld() == null) {
            plugin.getLogger().warning("The world for location " + location + " is null!");
            return tier;
        }

        RegionManager manager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(location.getWorld()));

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

    public boolean canBeRestocked(Chest chest) {
        for (ChestTracker tracker : chestTrackers) {
            if (!tracker.canRestockChest(chest)) {
                return false;
            }
        }
        return true;
    }

    private void logIfMuchLogging(String message) {
        if (plugin.getConfig().getBoolean("settings.much-logging")) {
            plugin.getLogger().info(message);
        }
    }

}
