// src/main/java/org/lpc/render/textures/Texture.java
package org.lpc.render.textures;

import lombok.Getter;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryStack.stackPush;

@Getter
public class Texture {
    private final int textureID;
    private final String filepath;

    public Texture(String filepath) {
        stbi_set_flip_vertically_on_load(true);  // Flip the image vertically
        textureID = loadTexture(filepath);
        this.filepath = filepath;
    }

    private int loadTexture(String filepath) {
        try (MemoryStack stack = stackPush()) {
            IntBuffer w = stack.mallocInt(1); // Width
            IntBuffer h = stack.mallocInt(1); // Height
            IntBuffer channels = stack.mallocInt(1); // Number of color channels

            ByteBuffer image = stbi_load(filepath, w, h, channels, STBI_rgb_alpha);
            if (image == null) {
                throw new RuntimeException("Failed to load texture file: " + filepath + "\n" + stbi_failure_reason());
            }

            int texID = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, texID);

            // Set texture parameters
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

            // Load texture data
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w.get(), h.get(), 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
            glGenerateMipmap(GL_TEXTURE_2D);

            stbi_image_free(image);

            return texID;
        }
    }

    public void bind() {
        glActiveTexture(GL_TEXTURE0);  // Activate texture unit 0
        glBindTexture(GL_TEXTURE_2D, textureID);
    }
}
