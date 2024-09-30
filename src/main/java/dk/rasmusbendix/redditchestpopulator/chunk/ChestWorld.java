package dk.rasmusbendix.redditchestpopulator.chunk;

import org.bukkit.World;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChestWorld {

    public static List<File> getRegionFiles(World world) {
        File regionFolder = new File(world.getWorldFolder(), "region");
        File[] files = regionFolder.listFiles((dir, name) -> name.endsWith(".mca"));

        if (files != null) {
            return Arrays.asList(files);
        }

        return new ArrayList<>();
    }

}
