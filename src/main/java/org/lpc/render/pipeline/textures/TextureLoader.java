package org.lpc.render.pipeline.textures;

import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;


import static org.lwjgl.opengl.GL12C.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30C.glGenerateMipmap;
import static org.lwjgl.system.MemoryStack.stackPush;

public class TextureLoader {

    public static Texture getTexture(String file) {
        try (MemoryStack stack = stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            // Load the image
            ByteBuffer image = STBImage.stbi_load(file, width, height, channels, 4);
            if (image == null) {
                throw new RuntimeException("Failed to load texture file: " + file + " - " + STBImage.stbi_failure_reason());
            }

            int textureID = glGenTextures(); // Generate a texture ID
            glBindTexture(GL_TEXTURE_2D, textureID); // Bind the texture

            // Set texture parameters
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

            // Upload the texture data
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(), height.get(), 0, GL_RGBA, GL_UNSIGNED_BYTE, image);

            // Generate mipmaps
            glGenerateMipmap(GL_TEXTURE_2D);

            STBImage.stbi_image_free(image); // Free the image memory

            return new Texture(textureID); // Return the texture
        }
    }
}

