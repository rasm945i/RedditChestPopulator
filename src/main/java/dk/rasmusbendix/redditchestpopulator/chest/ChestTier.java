package dk.rasmusbendix.redditchestpopulator.chest;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ChestTier {

    @Getter
    private final String regionPrefix;
    private final HashMap<ItemStack, Double> lootTable;
    @Getter
    @Setter
    private boolean preventEmpty;
    @Getter
    private UUID identifier;
    private final Random random = new Random();

    public ChestTier(String regionPrefix) {
        this.regionPrefix = regionPrefix;
        this.lootTable = new HashMap<>();
        this.preventEmpty = false;
        this.identifier = UUID.randomUUID();
    }

    public void addItem(ItemStack stack, double chance) {
        lootTable.put(stack, chance);
    }

    public static ChestTier fromConfig(ConfigurationSection section) {

        ChestTier tier = new ChestTier(section.getString("region-prefix"));
        tier.identifier = UUID.fromString(section.getName());

        for (String key : section.getConfigurationSection("content").getKeys(false)) {
            tier.addItem(
                    section.getItemStack("content." + key + ".item"),
                    section.getDouble("content." + key + ".chance")
            );
        }

        tier.setPreventEmpty(section.getBoolean("prevent-empty", false));
        return tier;

    }

    public void save(FileConfiguration config) {
        String path = "tiers." + identifier.toString();
        config.set(path + ".region-prefix", this.regionPrefix);
        config.set(path + ".prevent-empty", this.preventEmpty);
        config.set(path + ".content", null);
        int index = 0;
        for (ItemStack stack : lootTable.keySet()) {
            config.set(path + ".content." + index + ".item", stack);
            config.set(path + ".content." + index + ".chance", lootTable.get(stack));
            index++;
        }
    }

    public ArrayList<ItemStack> generateLootTable() {
        ArrayList<ItemStack> list = new ArrayList<>();
        for (ItemStack stack : lootTable.keySet()) {
            if (random.nextDouble() <= lootTable.get(stack)) {
                list.add(stack.clone());
            }
        }

        if (preventEmpty && list.isEmpty()) {

            int index = 0;
            int desiredIndex = random.nextInt(lootTable.size());

            for (ItemStack stack : lootTable.keySet()) {
                if (index == desiredIndex) {
                    list.add(stack);
                    break;
                }
                index++;
            }

        }

        return list;
    }

    public void fillChest(Chest chest, List<ItemStack> loot) {

        for(ItemStack stack : loot) {
            int slot = getNextValidSlot(chest.getInventory());
            chest.getInventory().setItem(slot, stack);
        }

    }

    private int getNextValidSlot(Inventory inventory) {
        int startSlot = random.nextInt(inventory.getSize());
        int slot = startSlot;
        while(inventory.getItem(slot) != null) {
            slot++;
            if(slot >= inventory.getSize()) {
                slot = 0;
            }
            if(slot == startSlot) {
                break;
            }
        }
        return slot;
    }

}
