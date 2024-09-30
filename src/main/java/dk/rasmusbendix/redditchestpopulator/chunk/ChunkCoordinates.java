package dk.rasmusbendix.redditchestpopulator.chunk;

import java.util.Objects;

public class ChunkCoordinates {
    public final int x;
    public final int z;

    public ChunkCoordinates(int x, int z) {
        this.x = x;
        this.z = z;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ChunkCoordinates that = (ChunkCoordinates) obj;
        return x == that.x && z == that.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, z);
    }
}
