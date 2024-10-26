package org.lpc.world.block.blocks;

import org.lpc.world.block.AbstractBlock;
import org.lpc.world.block.BlockType;

public class WaterBlock extends AbstractBlock {
    public WaterBlock(int x, int y, int z) {
        super(x, y, z, 5, BlockType.LIQUID);
    }
}
