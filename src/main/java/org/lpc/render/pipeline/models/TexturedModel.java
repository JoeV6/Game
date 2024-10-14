package org.lpc.render.pipeline.models;

import lombok.Getter;
import lombok.Setter;
import org.lpc.render.pipeline.textures.Texture;

@Getter @Setter
public class TexturedModel {
    private RawModel rawModel;
    private Texture texture;

    public TexturedModel(RawModel rawModel, Texture texture) {
        this.rawModel = rawModel;
        this.texture = texture;
    }
}
