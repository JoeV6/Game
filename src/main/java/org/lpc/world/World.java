package org.lpc.world;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.lpc.world.block.AbstractBlock;
import org.lpc.world.block.blocks.GrassBlock;

@Getter @Setter
public class World {
    AbstractBlock[][][] blocks;

    public World(int width, int height, int depth) {
        blocks = new AbstractBlock[width][height][depth];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < depth; z++) {
                    if (y==0 || x==0 || z==0) {
                        blocks[x][y][z] = new GrassBlock(x, y, z);
                        System.out.println("Block at " + x + ", " + y + ", " + z + " is a grass block");
                    }
                }
            }
        }
    }

    public AbstractBlock getBlock(int x, int y, int z) {
        return blocks[x][y][z];
    }
}
