package org.lpc.render.pipeline.textures;

import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.glTexSubImage3D;
import static org.lwjgl.opengl.GL12C.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL12C.glTexImage3D;
import static org.lwjgl.opengl.GL30C.GL_TEXTURE_2D_ARRAY;
import static org.lwjgl.opengl.GL30C.glGenerateMipmap;
import static org.lwjgl.system.MemoryStack.stackPush;

public class TextureLoader {
    public static final int ATLAS_SIZE = 64;

    public static Texture getTexture(String file) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(file));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Convert BufferedImage to ByteBuffer
        assert image != null;
        int width = image.getWidth();
        int height = image.getHeight();
        int layers = height / 64; // Assuming each texture is 64x64 pixels (texture size is square)
        ByteBuffer buffer = ByteBuffer.allocateDirect(width * height * 4);

        for (int i = 0; i < layers; i++) {
            for (int y = 0; y < 64; y++) {
                for (int x = 0; x < width; x++) {
                    int pixel = image.getRGB(x, i * 64 + y); // Extract each layer
                    buffer.put((byte) ((pixel >> 16) & 0xFF)); // Red
                    buffer.put((byte) ((pixel >> 8) & 0xFF));  // Green
                    buffer.put((byte) (pixel & 0xFF));         // Blue
                    buffer.put((byte) ((pixel >> 24) & 0xFF)); // Alpha
                }
            }
        }

        buffer.flip(); // Prepare the buffer for reading

        int textureArrayId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D_ARRAY, textureArrayId);

        // Allocate storage for the texture array
        glTexImage3D(GL_TEXTURE_2D_ARRAY, 0, GL_RGBA, width, 64, layers, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        // Set texture parameters
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        glBindTexture(GL_TEXTURE_2D_ARRAY, 0); // Unbind the texture

        // Output each layer of the texture array to separate PNGs
        //outputTextureLayersToPNG(buffer, width, layers);

        return new Texture(textureArrayId);
    }

    private static void outputTextureLayersToPNG(ByteBuffer buffer, int width, int layers) {
        for (int layer = 0; layer < layers; layer++) {
            BufferedImage outputImage = new BufferedImage(width, 64, BufferedImage.TYPE_INT_ARGB);

            // Reset buffer position for reading the current layer
            buffer.position(layer * width * 64 * 4); // Move to the start of the current layer

            // Read pixel data from buffer for this layer
            for (int y = 0; y < 64; y++) {
                for (int x = 0; x < width; x++) {
                    int r = buffer.get() & 0xFF; // Red
                    int g = buffer.get() & 0xFF; // Green
                    int b = buffer.get() & 0xFF; // Blue
                    int a = buffer.get() & 0xFF; // Alpha
                    int pixel = (a << 24) | (r << 16) | (g << 8) | b;
                    outputImage.setRGB(x, y, pixel);
                }
            }

            // Save the current layer as a PNG
            File outputfile = new File("output_texture_layer_" + layer + ".png");
            try {
                ImageIO.write(outputImage, "png", outputfile);
                System.out.println("Texture layer " + layer + " saved as 'output_texture_layer_" + layer + ".png'");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

