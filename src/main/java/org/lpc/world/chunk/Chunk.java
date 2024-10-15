package org.lpc.world.chunk;

import lombok.Getter;
import org.lpc.utils.PerlinNoise;
import org.lpc.world.block.AbstractBlock;
import org.lpc.world.block.blocks.DirtBlock;
import org.lpc.world.block.blocks.GrassBlock;

@Getter
public class Chunk {
    public static final int CHUNK_SIZE = 16;
    public static final int CHUNK_HEIGHT = 10;

    private final AbstractBlock[][][] blocks;
    private final int chunkX, chunkZ;

    public Chunk(int chunkX, int chunkZ) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;

        blocks = new AbstractBlock[CHUNK_SIZE][CHUNK_SIZE][CHUNK_HEIGHT];
        init();
    }

    private void init() {
        PerlinNoise perlinNoise = new PerlinNoise();

        // Generate height for the current chunk based on its position
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int z = 0; z < CHUNK_SIZE; z++) {
                int nx = chunkX * CHUNK_SIZE + x;
                int nz = chunkZ * CHUNK_SIZE + z;

                // Generate height using Perlin noise
                double height = perlinNoise.noise(nx * 0.1, nz * 0.1) * CHUNK_HEIGHT;

                // Smooth out height by adding variations
                height += (perlinNoise.noise(nx * 0.1 * 2, nz * 0.1 * 2) * 0.5) * CHUNK_HEIGHT; // Add finer detail
                height = Math.max(0, Math.min(height, CHUNK_HEIGHT - 1)); // Clamp height to valid range

                for (int y = 0; y < CHUNK_HEIGHT; y++) {
                    if (y < height) { // Fill blocks below the height
                        if (y == (int) height - 1) {
                            blocks[x][z][y] = new GrassBlock(nx, y, nz); // Top layer grass
                        } else if (y < (int) height - 1 && y > (int) height - 5) {
                            blocks[x][z][y] = new DirtBlock(nx, y, nz); // Below grass, fill with dirt (null for fps)
                        } else {
                            blocks[x][z][y] = null; // No block above the height
                        }
                    } else {
                        blocks[x][z][y] = null; // No block above the ground level
                    }
                }
            }
        }
    }

    public AbstractBlock getBlock(int x, int y, int z) {
        return blocks[x][y][z];
    }

    public void setBlock(int x, int y, int z, AbstractBlock block) {
        blocks[x][y][z] = block;
    }
}
