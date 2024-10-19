package org.lpc.world.chunk;

import lombok.Getter;
import org.lpc.utils.PerlinNoise;
import org.lpc.world.block.AbstractBlock;
import org.lpc.world.block.blocks.DirtBlock;
import org.lpc.world.block.blocks.GrassBlock;

@Getter
public class Chunk {
    public static final int CHUNK_SIZE = 16;
    public static final int CHUNK_HEIGHT = 64;

    private static final PerlinNoise perlinNoise = new PerlinNoise();

    private final AbstractBlock[][][] blocks;
    private final int chunkX, chunkZ;

    public Chunk(int chunkX, int chunkZ) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;

        blocks = new AbstractBlock[CHUNK_SIZE][CHUNK_SIZE][CHUNK_HEIGHT];
        generateChunk();
    }

    private void generateChunk() {
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int z = 0; z < CHUNK_SIZE; z++) {
                int worldX = chunkX * CHUNK_SIZE + x;
                int worldZ = chunkZ * CHUNK_SIZE + z;


                double height = perlinNoise.noise(worldX * 0.1, worldZ * 0.1) * CHUNK_HEIGHT / 4;

                height = Math.max(1, Math.min(height, CHUNK_HEIGHT - 1));

                for (int y = 0; y < CHUNK_HEIGHT; y++) {
                    if (y < height) {
                        if (y == (int) height - 1) {
                            blocks[x][z][y] = new GrassBlock(worldX, y, worldZ); // Grass on top layer
                        } else if (y < (int) height - 1 && y > (int) height - 10) {
                            blocks[x][z][y] = new DirtBlock(worldX, y, worldZ); // Dirt layer under grass
                        } else {
                            blocks[x][z][y] = null; // Empty block
                        }
                    } else {
                        blocks[x][z][y] = null; // Empty block above ground level
                    }
                }
            }
        }
    }

    public AbstractBlock getBlock(int x, int y, int z) {
        if (isOutOfBounds(x, y, z)) {
            return null;
        }
        return blocks[x][z][y];
    }

    public void setBlock(int x, int y, int z, AbstractBlock block) {
        if (!isOutOfBounds(x, y, z)) {
            blocks[x][z][y] = block;
        }
    }

    public void removeBlock(int x, int y, int z) {
        setBlock(x, y, z, null);
    }

    private boolean isOutOfBounds(int x, int y, int z) {
        return x < 0 || x >= CHUNK_SIZE || y < 0 || y >= CHUNK_HEIGHT || z < 0 || z >= CHUNK_SIZE;
    }

    public AbstractBlock getBlockWorld(int worldX, int worldY, int worldZ) {
        int localX = worldX % CHUNK_SIZE;
        int localZ = worldZ % CHUNK_SIZE;
        return getBlock(localX, worldY, localZ);
    }

    public void setBlockWorld(int worldX, int worldY, int worldZ, AbstractBlock block) {
        int localX = worldX % CHUNK_SIZE;
        int localZ = worldZ % CHUNK_SIZE;
        setBlock(localX, worldY, localZ, block);
    }

    public void removeBlockWorld(int worldX, int worldY, int worldZ) {
        setBlockWorld(worldX, worldY, worldZ, null);
    }
}
