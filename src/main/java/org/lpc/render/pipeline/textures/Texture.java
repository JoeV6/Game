package org.lpc.render.pipeline.textures;

import lombok.Getter;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL13C;

@Getter
public class Texture {
    private final int textureID;

    public Texture(int textureID) {
        this.textureID = textureID;
    }

    public void bind() {
        GL13C.glActiveTexture(GL13C.GL_TEXTURE0); // Activate the first texture unit
        GL11C.glBindTexture(GL11C.GL_TEXTURE_2D, textureID); // Bind the texture
    }

    public void cleanup() {
        GL11C.glDeleteTextures(textureID); // Cleanup the texture when done
    }
}

