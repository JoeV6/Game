package org.lpc.world.block;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;
import org.lpc.Game;
import org.lpc.render.pipeline.models.CubeModel;
import org.lpc.world.World;

import java.io.Serializable;

@Getter @Setter
public abstract class AbstractBlock implements Serializable {
    private int x,y,z;
    private int blockID;

    private CubeModel cubeModel;

    public AbstractBlock(int x, int y, int z, int blockID) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.blockID = blockID;

        this.cubeModel = new CubeModel(new Vector3f(x, y, z), blockID);
    }

    public AbstractBlock[] getNeighbouringBlocks(){
        Game game = Game.getInstance();
        World world = game.getWorld();

        AbstractBlock[] blocks = new AbstractBlock[6];

        blocks[0] = world.getBlockAt(x, y, z + 1);
        blocks[1] = world.getBlockAt(x, y, z - 1);
        blocks[2] = world.getBlockAt(x, y + 1, z);
        blocks[3] = world.getBlockAt(x, y - 1, z);
        blocks[4] = world.getBlockAt(x + 1, y, z);
        blocks[5] = world.getBlockAt(x - 1, y, z);

        return blocks;
    }
}
