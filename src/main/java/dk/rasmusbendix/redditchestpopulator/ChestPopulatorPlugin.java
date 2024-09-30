package dk.rasmusbendix.redditchestpopulator;

import dk.rasmusbendix.redditchestpopulator.chest.TierManager;
import dk.rasmusbendix.redditchestpopulator.restock.ChestRestocker;
import dk.rasmusbendix.redditchestpopulator.command.AddTierCommand;
import dk.rasmusbendix.redditchestpopulator.command.TestLootCommand;
import dk.rasmusbendix.redditchestpopulator.tracking.PlayerChestTracker;
import dk.rasmusbendix.redditchestpopulator.tracking.RestockTracker;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ChestPopulatorPlugin extends JavaPlugin {

    @Getter private TierManager tierManager;
    @Getter private ChestRestocker chestRestocker;
    private PlayerChestTracker playerChests;
    private RestockTracker restockedChests;

    @Override
    public void onEnable() {
        CustomConfig.setPlugin(this);
        saveDefaultConfig();
        tierManager = new TierManager(this);
        getServer().getPluginCommand("addchesttier").setExecutor(new AddTierCommand(this));
        getServer().getPluginCommand("testtier").setExecutor(new TestLootCommand(this));
        this.playerChests = new PlayerChestTracker(this);
        this.restockedChests = new RestockTracker(this);
        this.chestRestocker = new ChestRestocker(this);

        this.chestRestocker.addTracker(playerChests);
        this.chestRestocker.addTracker(restockedChests);

    }

    @Override
    public void onDisable() {
        tierManager.save();
        playerChests.save();
        restockedChests.save();
    }

    public void sendMessage(Player receiver, String path) {
        receiver.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("message." + path, path + " - message not set")));
    }

}
