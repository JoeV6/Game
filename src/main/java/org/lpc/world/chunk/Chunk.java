package org.lpc.world.chunk;

import lombok.Getter;
import lombok.Setter;
import org.lpc.world.block.AbstractBlock;
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

    private void init(){
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int z = 0; z < CHUNK_SIZE; z++) {
                for(int y = 0; y < CHUNK_HEIGHT; y++){
                    blocks[x][z][y] = null;
                }
                int nx = chunkX * CHUNK_SIZE + x;
                int nz = chunkZ * CHUNK_SIZE + z;
                blocks[x][z][0] = new GrassBlock(nx, 0, nz);
            }
        }
    }

    public void load(){

    }

    public AbstractBlock getBlock(int x, int y, int z) {
        return blocks[x][y][z];
    }

    public void setBlock(int x, int y, int z, AbstractBlock block) {
        blocks[x][y][z] = block;
    }
}
