package org.lpc.world.block.blocks;

import org.lpc.world.block.AbstractBlock;
import org.lpc.world.block.BlockType;

public class AirBlock extends AbstractBlock {

    public AirBlock(int x, int y, int z) {
        super(x, y, z, 4, BlockType.GAS);
    }
}
