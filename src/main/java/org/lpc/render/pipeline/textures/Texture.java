package org.lpc.render.pipeline.textures;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public class Texture implements Serializable {
    private int textureID;

    public Texture(int textureID) {
        this.textureID = textureID;
    }
}
