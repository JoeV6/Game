package org.lpc.world.chunk;

import lombok.Getter;
import org.lpc.Game;
import org.lpc.utils.PerlinNoise;
import org.lpc.world.block.AbstractBlock;
import org.lpc.world.block.blocks.AirBlock;
import org.lpc.world.block.blocks.CobbleStoneBlock;
import org.lpc.world.block.blocks.DirtBlock;
import org.lpc.world.block.blocks.GrassBlock;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

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

                //plains
                //double height = perlinNoise.noise(worldX * 0.1, worldZ * 0.1) * CHUNK_HEIGHT / 20;
                //mountains
                //double height = perlinNoise.noise(worldX * 0.1, worldZ * 0.1) * CHUNK_HEIGHT / 2;
                //hills
                double height = perlinNoise.noise(worldX * 0.1, worldZ * 0.1) * CHUNK_HEIGHT / 4;


                height = Math.max(1, Math.min(height, CHUNK_HEIGHT - 1));

                for (int y = 0; y < CHUNK_HEIGHT; y++) {
                    if (y < height) {
                        if(blocks[x][z][y] != null){
                            continue;
                        }
                        if (y == (int) height) {
                            blocks[x][z][y] = new GrassBlock(worldX, y, worldZ);
                        } else if (y < (int) height && y > (int) height - 10) {
                            blocks[x][z][y] = new DirtBlock(worldX, y, worldZ);
                        } else {
                            blocks[x][z][y] = new CobbleStoneBlock(worldX, y, worldZ); // Stone below ground level
                        }
                    } else {
                        blocks[x][z][y] = new AirBlock(worldX, y, worldZ); // Empty block above ground level
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
        if (isOutOfBounds(x, y, z)) return;

        blocks[x][z][y] = block;
    }

    private boolean isOutOfBounds(int x, int y, int z) {
        return x < 0 || x >= CHUNK_SIZE || y < 0 || y >= CHUNK_HEIGHT || z < 0 || z >= CHUNK_SIZE;
    }
}
