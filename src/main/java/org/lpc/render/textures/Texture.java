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

    public Texture(String filepath) {
        textureID = loadTexture(filepath);
    }

    private int loadTexture(String filepath) {
        try (MemoryStack stack = stackPush()) {
            IntBuffer w = stack.mallocInt(1); // Width
            IntBuffer h = stack.mallocInt(1); // Height
            IntBuffer channels = stack.mallocInt(1); // Number of color channels

            // Load the image file
            ByteBuffer image = stbi_load(filepath, w, h, channels, 0);
            if (image == null) {
                throw new RuntimeException("Failed to load texture file: " + filepath + "\n" + stbi_failure_reason());
            }

            int texID = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, texID);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w.get(), h.get(), 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
            glGenerateMipmap(GL_TEXTURE_2D);
            stbi_image_free(image); // Free the image memory

            return texID;
        }
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, textureID);
    }
}
