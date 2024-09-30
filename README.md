# How to use
- Fill your inventory with items you want in a certain type of chest
- Type the `/addchesttier [tier-prefix]` to add the tier
- Create a region. If your chest-tier was named "5", the region name should start with "5" for it to receive that chests content
- Chests outside of regions receive nothing, or whatever is specified in the "default" tier.

# Commands
- `/addchesttier [tier-prefix] <prevent-empty true|false>` - Add a chest-tier. If `true` is passed as second argument, it will ensure that type of chest-tier will not be empty.<br>If the chest receives no generated loot, a random (not weighted) item from the loot table will be picked.

# How it works
When a chunk is loaded, no matter how, all TileEntities are fetched to find all chests.  
Each chest is iterated to check what region it is in.  
Depending on which region they are in, they are filled with loot based on ChestTiers defined with the only command available.  
After the chest is filled with loot, it is flagged so it wont get restocked, and saved in `world-generated-chests.yml`.

When a player places a chest, it is saved in `player-placed-chests.yml` so they won't get restocked on chunk-load.

If you would ever want to restock world-chests, simply delete `world-generated-chests.yml`.  
If you would ever want to restock player-placed chests, simply delete `player-placed-chests.yml`.

# Known possible issues
- Overlapping regions - I have no clue which chest-tier will be used. It will most likely be the last in the list of applicable regions when sorted alphabetically. If `clear-chests-before-restock` is false, the outcome is probably the same.

# Default config
`config.yml`
```yaml
settings:
  # Whether to put items in to chests. I recommend setting "false" while setting up regions.
  populate-chests: false
  # Should chests get cleared (of potential old loot) before being restocked?
  clear-chests-before-restock: true
  # If true, logs information about how many chests are restocked or skipped
  much-logging: true
message:
  no-permission: "You don't have permission to use this command!"
  specify-tier: "Specify the tier you wish to test"
  look-at-chest: "You must look at a chest to use this test command"
  tier-not-found: "The tier you specified was not found"
  test-chest-filled: "Filled the chest you're looking at with the tier you specified!"
```
`world-generated-chests.yml` and `player-placed-chests.yml` are empty/deleted by default.