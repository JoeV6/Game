package org.lpc.render.pipeline.textures;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Texture {
    private int textureID;

    public Texture(int textureID) {
        this.textureID = textureID;
    }
}
