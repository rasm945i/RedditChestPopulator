package dk.rasmusbendix.redditchestpopulator.command;

import dk.rasmusbendix.redditchestpopulator.ChestPopulatorPlugin;
import dk.rasmusbendix.redditchestpopulator.chunk.ChestWorld;
import dk.rasmusbendix.redditchestpopulator.chunk.ChunkCoordinates;
import dk.rasmusbendix.redditchestpopulator.chunk.RegionReader;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class RegionTestCommand implements CommandExecutor {

    private ChestPopulatorPlugin plugin;
    private List<Location> chestLocations;
    private Set<Material> tileEntities;
    private int currentChest = 0;
    private Set<ChunkCoordinates> alreadyPassed;

    AtomicInteger chestsFound = new AtomicInteger();
    AtomicInteger chunksLoaded = new AtomicInteger();
    AtomicInteger chunksFailedToLoad = new AtomicInteger();
    AtomicInteger chunksAlreadyLoaded = new AtomicInteger();
    AtomicInteger skips = new AtomicInteger();

    public RegionTestCommand(ChestPopulatorPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(args[0].equalsIgnoreCase("next")) {
            Player player = (Player) sender;
            player.teleport(chestLocations.get(currentChest));
            currentChest++;
            return true;
        }

        if(args[0].equalsIgnoreCase("what")) {
            for(Material material : tileEntities) {
                plugin.getLogger().info("Tile: " + material);
            }
            return true;
        }

        if(args[0].equalsIgnoreCase("list")) {
            for (World world : plugin.getServer().getWorlds()) {
                plugin.getLogger().info("World: " + world.getName());
            }
            return true;
        }

        if(args[0].equalsIgnoreCase("print")) {
            printThings();
            return true;
        }

        if(args[0].equalsIgnoreCase("listener")) {
            Player player = (Player) sender;
            player.teleport(plugin.getChunkEvents().nextChestLocation());
            return true;
        }

        currentChest = 0;
        World world = plugin.getServer().getWorld(args[0]);
        List<File> files = ChestWorld.getRegionFiles(world);
        chestLocations = new ArrayList<>();
        tileEntities = new HashSet<>();
        alreadyPassed = new HashSet<>();

        chestsFound = new AtomicInteger();
        chunksLoaded = new AtomicInteger();
        chunksFailedToLoad = new AtomicInteger();
        chunksAlreadyLoaded = new AtomicInteger();
        skips = new AtomicInteger();

        int count = 0;

//        for(File file : files) {
//            if(count > 20) {
//                plugin.getLogger().info("Hit 20 limit");
//                break;
//            }
//            RegionReader reader = new RegionReader(file);
//            try {
//                for (ChunkCoordinates coordinates : reader.getChunksInRegion()) {
//
//                    world.getChunkAtAsync(coordinates.x, coordinates.z, (c) -> {
//                        if (!c.isLoaded()) {
//                            chunksFailedToLoad.getAndIncrement();
//                            return;
//                        }
//
//                        // Ensure the modification happens on the main server thread
//                        Bukkit.getScheduler().runTask(plugin, () -> {
//                            for(BlockState state : c.getTileEntities()) {
//                                tileEntities.add(state.getType());
//                                if(state instanceof Chest) {
//                                    Chest chest = (Chest) state;
//                                    chest.getBlockInventory().setItem(1, new ItemStack(Material.STICK, 42));
//                                    chest.update(true);
//                                    chestsFound.getAndIncrement();
//                                    chestLocations.add(chest.getLocation());
//                                }
//                            }
//                        });
//
//                        // Unload the chunk after processing
//                        Bukkit.getScheduler().runTask(plugin, () -> {
//                            if (c.isLoaded()) {
//                                c.unload(true);
//                            }
//                        });
//                    });
//                }
//            } catch (IOException e) {
//                sender.sendMessage("Failed to read region file!");
//                e.printStackTrace();
//            }
//            count++;
//        }

        printThings();
        sender.sendMessage("Put sticks into " + chestsFound + " chests!");

        return true;
    }

    private void printThings() {
        plugin.getLogger().info("Put sticks into " + chestsFound + " chests!");
        plugin.getLogger().info("Skipped " + skips + " chunks.");
        plugin.getLogger().info("Loaded " + chunksLoaded + " chunks!");
        plugin.getLogger().info("Failed to load " + chunksFailedToLoad + " chunks!");
        plugin.getLogger().info(chunksAlreadyLoaded + " chunks were already loaded!");
    }

}
