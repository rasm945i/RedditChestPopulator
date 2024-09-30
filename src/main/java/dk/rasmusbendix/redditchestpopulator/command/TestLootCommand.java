package dk.rasmusbendix.redditchestpopulator.command;

import dk.rasmusbendix.redditchestpopulator.ChestPopulatorPlugin;
import dk.rasmusbendix.redditchestpopulator.chest.ChestTier;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

import java.util.Optional;

public class TestLootCommand implements CommandExecutor {

    private final ChestPopulatorPlugin plugin;

    public TestLootCommand(ChestPopulatorPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("chestfiller.testtier")) {
            plugin.sendMessage(player, "no-permission");
            return true;
        }

        if (args.length < 1) {
            plugin.sendMessage(player, "specify-tier");
            return true;
        }

        Optional<ChestTier> tierOptional = plugin.getTierManager().getTierForRegion(args[0].toLowerCase());
        if (!tierOptional.isPresent()) {
            plugin.sendMessage(player, "tier-not-found");
            return true;
        }

        BlockIterator bi = new BlockIterator(player, 10);
        boolean foundChest = false;
        while (bi.hasNext()) {
            Block block = bi.next();
            if (block.getType() == Material.CHEST) {
                Chest chest = (Chest) block.getState();
                ChestTier tier = tierOptional.get();
                tier.fillChest(chest, tier.generateLootTable());
                foundChest = true;
                break;
            }
        }

        if (foundChest) {
            plugin.sendMessage(player, "test-chest-filled");
            return true;
        }

        plugin.sendMessage(player, "look-at-chest");
        return true;

    }
    
}
