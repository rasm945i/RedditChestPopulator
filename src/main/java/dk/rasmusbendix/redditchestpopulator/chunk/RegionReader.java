package dk.rasmusbendix.redditchestpopulator.chunk;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.Set;

public class RegionReader {

    private Set<ChunkCoordinates> coordinates;
    private final File regionFile;

    public RegionReader(File file) {
        this.regionFile = file;
        this.coordinates = new HashSet<>();
    }

    public Set<ChunkCoordinates> getChunksInRegion() throws IOException {

        if(!this.coordinates.isEmpty()) {
            return this.coordinates;
        }

        Set<ChunkCoordinates> chunkCoordinates = new HashSet<>();

        try (RandomAccessFile raf = new RandomAccessFile(regionFile, "r")) {
            for (int i = 0; i < 1024; i++) {  // The header has 1024 4-byte entries (one for each chunk in the 32x32 grid)
                raf.seek(i * 4);
                int offset = raf.readByte() << 16 | (raf.readByte() & 0xFF) << 8 | (raf.readByte() & 0xFF);
                int sectorCount = raf.readByte();

                if (offset != 0 && sectorCount > 0) {
                    int chunkX = (i % 32);
                    int chunkZ = (i / 32);
                    chunkCoordinates.add(new ChunkCoordinates(chunkX, chunkZ));
                }
            }
        }
        this.coordinates = chunkCoordinates;
        return this.coordinates;
    }

}
