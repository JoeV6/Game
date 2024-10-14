package org.lpc.world.block;

import lombok.Getter;
import lombok.Setter;
import org.lpc.Game;
import org.lpc.render.pipeline.ModelLoader;
import org.lpc.render.pipeline.models.premade.CubeModel;
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

        ModelLoader m = Game.getInstance().getModelLoader();
        String textureLocation = "blocks/block_" + blockID;
        this.texture =  new Texture(m.loadTexture(textureLocation));
        this.cubeModel = new CubeModel(x, y, z, 1, texture);
    }
}
