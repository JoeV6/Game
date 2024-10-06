package org.lpc.render.pipeline.models;

import lombok.Getter;
import lombok.Setter;
import org.lpc.render.pipeline.textures.ModelTexture;

@Getter @Setter
public class TexturedModel {
    private RawModel rawModel;
    private ModelTexture texture;

    public TexturedModel(RawModel rawModel, ModelTexture texture) {
        this.rawModel = rawModel;
        this.texture = texture;
    }
}
