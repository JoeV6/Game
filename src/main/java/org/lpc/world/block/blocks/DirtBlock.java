package org.lpc.world.block.blocks;

import org.lpc.world.block.AbstractBlock;
import org.lpc.world.block.BlockType;

public class DirtBlock extends AbstractBlock {
    public DirtBlock(int x, int y, int z) {
        super(x, y, z, 1, BlockType.SOLID);
    }
}
