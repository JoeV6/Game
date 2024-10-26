package org.lpc.world.block.blocks;

import org.lpc.world.block.AbstractBlock;
import org.lpc.world.block.BlockType;

public class GrassBlock extends AbstractBlock {
    public GrassBlock(int x, int y, int z) {
        super(x, y, z, 0, BlockType.SOLID);
    }
}
