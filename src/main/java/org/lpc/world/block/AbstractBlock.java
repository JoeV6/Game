package org.lpc.world.block;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;
import org.lpc.render.pipeline.models.CubeModel;
import org.lpc.render.pipeline.textures.Texture;

@Getter @Setter
public abstract class AbstractBlock {
    private int x,y,z;
    private int blockID;

    private CubeModel cubeModel;
    private Texture texture;

    public AbstractBlock(int x, int y, int z, int blockID) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.blockID = blockID;


        String textureLocation = "blocks/block_" + blockID;
        this.texture =  new Texture(0);
        this.cubeModel = new CubeModel(new Vector3f(x, y, z));
    }
}
