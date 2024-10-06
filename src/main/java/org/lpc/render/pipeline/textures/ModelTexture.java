package org.lpc.render.pipeline.textures;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ModelTexture {

    private int textureID;

    public ModelTexture(int textureID) {
        this.textureID = textureID;
    }
}
