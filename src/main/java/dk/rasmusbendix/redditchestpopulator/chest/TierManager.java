package dk.rasmusbendix.redditchestpopulator.chest;

import dk.rasmusbendix.redditchestpopulator.ChestPopulatorPlugin;

import java.util.HashMap;
import java.util.Optional;

public class TierManager {

    private final HashMap<String, ChestTier> tierMap;
    private final ChestPopulatorPlugin plugin;

    public TierManager(ChestPopulatorPlugin plugin) {
        this.plugin = plugin;
        this.tierMap = new HashMap<>();
        loadChestTiers();
    }

    private void loadChestTiers() {

        if(plugin.getConfig().getConfigurationSection("tiers") == null) {
            plugin.getLogger().info("No tiers are defined yet.");
            return;
        }

        for(String uuid : plugin.getConfig().getConfigurationSection("tiers").getKeys(false)) {
            ChestTier tier = ChestTier.fromConfig(plugin.getConfig().getConfigurationSection("tiers." + uuid));
            addTier(tier);
        }

        plugin.getLogger().info("Loaded " + tierMap.size() + " chest tiers!");

    }

    public void save() {
        for(ChestTier tier : tierMap.values()) {
            tier.save(plugin.getConfig());
        }
        plugin.saveConfig();
    }

    public void addTier(ChestTier tier) {
        tierMap.put(tier.getRegionPrefix(), tier);
    }

    public Optional<ChestTier> getTierForRegion(String region) {
        for(String key : tierMap.keySet()) {
            if(region.startsWith(key)) {
                return Optional.of(tierMap.get(key));
            }
        }
        return Optional.empty();
    }

    public ChestTier getDefault() {
        Optional<ChestTier> opt = getTierForRegion("default");
        if(opt.isPresent())
            return opt.get();
        return new ChestTier("somethingverylongthatisemptyanduselessandhasnoitemssowedontgetanerrorhappyface");
    }

}
