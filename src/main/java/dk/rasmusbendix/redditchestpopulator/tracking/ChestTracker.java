package dk.rasmusbendix.redditchestpopulator.tracking;

import org.bukkit.block.Chest;

public interface ChestTracker {

    boolean canRestockChest(Chest chest);

}
