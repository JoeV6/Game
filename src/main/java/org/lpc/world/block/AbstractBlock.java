package org.lpc.world.block;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;
import org.lpc.render.pipeline.models.CubeModel;
import org.lpc.render.pipeline.textures.Texture;

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
}
