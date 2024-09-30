package dk.rasmusbendix.redditchestpopulator;

import dk.rasmusbendix.redditchestpopulator.chest.TierManager;
import dk.rasmusbendix.redditchestpopulator.chunk.ChunkEvents;
import dk.rasmusbendix.redditchestpopulator.command.AddTierCommand;
import dk.rasmusbendix.redditchestpopulator.command.RegionTestCommand;
import dk.rasmusbendix.redditchestpopulator.command.TestLootCommand;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("LombokGetterMayBeUsed")
public class ChestPopulatorPlugin extends JavaPlugin {

    // TODO Iterate all generated chunks to find chests
    // TODO Iterate all *regions*? and find chests?

    @Getter private TierManager tierManager;
    @Getter private ChunkEvents chunkEvents;

    @Override
    public void onEnable() {
        CustomConfig.setPlugin(this);
        saveDefaultConfig();
        tierManager = new TierManager(this);
        getServer().getPluginCommand("addchesttier").setExecutor(new AddTierCommand(this));
        getServer().getPluginCommand("testtier").setExecutor(new TestLootCommand(this));
        getServer().getPluginCommand("regiontest").setExecutor(new RegionTestCommand(this));
        this.chunkEvents = new ChunkEvents(this);
    }

    @Override
    public void onDisable() {
        tierManager.save();
        chunkEvents.saveIgnoredChests();
    }

    public void sendMessage(Player receiver, String path) {
        receiver.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("message." + path, path + " - message not set")));
    }

}
