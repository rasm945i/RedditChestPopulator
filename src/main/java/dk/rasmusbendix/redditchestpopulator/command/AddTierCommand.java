package dk.rasmusbendix.redditchestpopulator.command;

import dk.rasmusbendix.redditchestpopulator.ChestPopulatorPlugin;
import dk.rasmusbendix.redditchestpopulator.chest.ChestTier;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AddTierCommand implements CommandExecutor {

    private final ChestPopulatorPlugin plugin;

    public AddTierCommand(ChestPopulatorPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)) {
            sender.sendMessage("Only players can execute this command!");
            return true;
        }

        Player player = (Player) sender;

        if(!player.hasPermission("chestfiller.addtier")) {
            plugin.sendMessage(player, "no-permission");
            return true;
        }

        if(args.length < 1) {
            plugin.sendMessage(player, "specify-tier-name");
            return true;
        }

        String tierName = args[0];
        ChestTier tier = new ChestTier(tierName);
        if(args.length > 1) {
            if(args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("1") || args[1].equalsIgnoreCase("yes")) {
                tier.setPreventEmpty(true);
            }
        }

        for(ItemStack stack : player.getInventory().getContents()) {
            if(stack == null || stack.getType() == Material.AIR) {
                continue;
            }
            plugin.sendMessage(player, "Added " + stack.getType());
            tier.addItem(stack.clone(), 0.5);
        }

        plugin.getTierManager().addTier(tier);
        plugin.getTierManager().save();
        plugin.sendMessage(player, "Created tier");
        return true;

    }

}
