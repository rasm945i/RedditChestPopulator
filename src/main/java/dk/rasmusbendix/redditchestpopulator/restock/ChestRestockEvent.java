package dk.rasmusbendix.redditchestpopulator.restock;

import dk.rasmusbendix.redditchestpopulator.chest.ChestTier;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.block.Chest;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ChestRestockEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled;

    @Getter private final Chest chest;
    @Getter @Setter private ChestTier tier;
    @Getter @Setter private List<ItemStack> generatedLoot;

    public ChestRestockEvent(Chest chest, ChestTier tier) {
        this.chest = chest;
        this.tier = tier;
        this.generatedLoot = tier.generateLootTable();
        this.cancelled = false;
    }

    public void addItemStackToLoot(ItemStack stack) {
        this.generatedLoot.add(stack.clone());
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
