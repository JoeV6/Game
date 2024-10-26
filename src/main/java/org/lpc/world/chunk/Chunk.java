package org.lpc.world.chunk;

import lombok.Getter;
import org.lpc.utils.PerlinNoise;
import org.lpc.world.block.AbstractBlock;
import org.lpc.world.block.blocks.*;

import java.io.Serializable;

@Getter
public class Chunk implements Serializable {
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
                generateBlockColumn(worldX, worldZ, x, z);
            }
        }
    }

    private void generateBlockColumn(int worldX, int worldZ, int x, int z) {
        double height = getTerrainHeight(worldX, worldZ);

        for (int y = 0; y < CHUNK_HEIGHT; y++) {
            if (y <= height) {
                if(blocks[x][z][y] != null){
                    continue;
                }
                if (y == (int) height) {
                    if(y > 15){
                        blocks[x][z][y] = new GrassBlock(worldX, y, worldZ);
                    }
                    else{
                        blocks[x][z][y] = new SandBlock(worldX, y, worldZ);
                    }
                } else if (y < (int) height && y > (int) height - 6) {
                    blocks[x][z][y] = new DirtBlock(worldX, y, worldZ);
                } else {
                    blocks[x][z][y] = new CobbleStoneBlock(worldX, y, worldZ); // Stone below ground level
                }
            } else {
                blocks[x][z][y] = new AirBlock(worldX, y, worldZ); // Empty block above ground level
            }
        }
    }

    double getTerrainHeight1(int worldX, int worldZ) {
        double height = perlinNoise.noise(worldX * 0.1, worldZ * 0.1) * CHUNK_HEIGHT / 4;
        height = Math.max(1, Math.min(height + 10, CHUNK_HEIGHT - 1));

        return height;
    }

    double getTerrainHeight(int worldX, int worldZ) {
        // Low-frequency base for mountains and large hills
        double baseHeight = perlinNoise.noise(worldX * 0.02, worldZ * 0.02) * CHUNK_HEIGHT / 2;

        // Medium-frequency layer for smaller hills
        double mediumLayer = perlinNoise.noise(worldX * 0.1, worldZ * 0.1) * (CHUNK_HEIGHT / 4);

        // High-frequency detail layer for finer undulations
        double detailLayer = perlinNoise.noise(worldX * 0.3, worldZ * 0.3) * (CHUNK_HEIGHT / 16);

        // Combine layers with varying intensities
        double height = baseHeight + mediumLayer * 0.7 + detailLayer * 0.3;

        // Ridge effect using absolute value for sharp peaks and valleys
        double ridgeLayer = Math.abs(perlinNoise.noise(worldX * 0.05, worldZ * 0.05)) * (CHUNK_HEIGHT / 8);

        // Mix ridge effect into height with a lower weight
        height += ridgeLayer * 0.5;

        // Clamp height to avoid overflow and apply a base level
        height = Math.max(1, Math.min(height, CHUNK_HEIGHT - 1));

        return height;
    }


    public AbstractBlock getBlock(int x, int y, int z) {
        if (isOutOfBounds(x, y, z)) {
            return null;
        }
        return blocks[x][z][y];
    }

    public void setBlock(int x, int y, int z, AbstractBlock block) {
        if (isOutOfBounds(x, y, z)) return;

        blocks[x][z][y] = block;
    }

    public void setBlock(AbstractBlock block){
        setBlock(block.getX(), block.getY(), block.getZ(), block);
    }

    private boolean isOutOfBounds(int x, int y, int z) {
        return x < 0 || x >= CHUNK_SIZE || y < 0 || y >= CHUNK_HEIGHT || z < 0 || z >= CHUNK_SIZE;
    }
}
