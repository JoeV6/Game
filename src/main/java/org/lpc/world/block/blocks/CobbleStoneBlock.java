package org.lpc.world.block.blocks;

import org.lpc.world.block.AbstractBlock;
import org.lpc.world.block.BlockType;

public class CobbleStoneBlock extends AbstractBlock {
    public CobbleStoneBlock(int x, int y, int z) {
        super(x, y, z, 2, BlockType.SOLID);
    }
}
