package org.lpc.utils;

import lombok.Getter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.List;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.glTexImage3D;
import static org.lwjgl.opengl.GL30C.GL_TEXTURE_2D_ARRAY;

@Getter
public class TextureAtlas {
    public static String ATLAS_PATH = "src/main/resources/textures/texture_atlas.png";
    private static TextureAtlas instance;

    private final Map<String, Rectangle> textureCoordinates;
    private BufferedImage atlasImage;
    private final List<Rectangle> textureRectangles;

    private TextureAtlas() {
        textureCoordinates = new HashMap<>();
        textureRectangles = new ArrayList<>();
    }

    public void createTextureAtlas(String path) {
        File dir = new File(path);
        File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".png"));

        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("No PNG files found in the specified directory.");
        }

        List<BufferedImage> images = new ArrayList<>();
        for (File file : files) {
            try {
                BufferedImage originalImage = ImageIO.read(file);
                // Resize to 64x64
                BufferedImage resizedImage = resizeImage(originalImage, 64, 64);
                images.add(resizedImage);
            } catch (IOException e) {
                System.out.println("Failed to load image: " + file.getName());
            }
        }

        int atlasWidth = 64;
        int atlasHeight = (images.size() * 64);

        atlasImage = new BufferedImage(atlasWidth, atlasHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = atlasImage.createGraphics();

        // Add images to the atlas and store coordinates
        int currentY = 0;
        for (BufferedImage image : images) {
            g.drawImage(image, 0, currentY, null);
            Rectangle rect = new Rectangle(0, currentY, 64, 64);
            textureRectangles.add(rect);
            textureCoordinates.put(image.toString(), rect);
            currentY += 64; // Move down by 64 pixels
        }

        g.dispose();
        System.out.println("Texture atlas created with dimensions: " + atlasWidth + "x" + atlasHeight);

        saveAtlasToFile(ATLAS_PATH);
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
        Image resultingImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(resultingImage, 0, 0, null);
        g2d.dispose();

        return outputImage;
    }

    private void saveAtlasToFile(String filePath) {
        try {
            ImageIO.write(atlasImage, "png", new File(filePath));
            System.out.println("Texture atlas saved to " + filePath);
        } catch (IOException e) {
            System.out.println("Failed to save texture atlas: " + e.getMessage());
        }
    }

    public int loadTextureAtlas(String filePath) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Convert BufferedImage to ByteBuffer
        int width = image.getWidth();
        int height = image.getHeight();
        int layers = height / 64; // Assuming each texture is 64x64 pixels
        ByteBuffer buffer = ByteBuffer.allocateDirect(width * height * 4);

        for (int i = 0; i < layers; i++) {
            for (int y = 0; y < 64; y++) {
                for (int x = 0; x < width; x++) {
                    int pixel = image.getRGB(x, i * 64 + y);
                    buffer.put((byte) ((pixel >> 16) & 0xFF)); // Red
                    buffer.put((byte) ((pixel >> 8) & 0xFF));  // Green
                    buffer.put((byte) (pixel & 0xFF));           // Blue
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

        return textureArrayId;
    }


    public float[] getTextureCoords(int id) {
        if (id < 0 || id >= textureRectangles.size()) {
            throw new IndexOutOfBoundsException("Invalid texture ID.");
        }

        Rectangle rect = textureRectangles.get(id);
        float u1 = rect.x / (float) atlasImage.getWidth();
        float v1 = rect.y / (float) atlasImage.getHeight();
        float u2 = (rect.x + rect.width) / (float) atlasImage.getWidth();
        float v2 = (rect.y + rect.height) / (float) atlasImage.getHeight();

        return new float[] {
                // Front face  4 vertices
                u1, v1,
                u1, v2,
                u2, v2,
                u2, v1,

                // Back face  4 vertices
                u1, v1,
                u1, v2,
                u2, v2,
                u2, v1,

                // Top face  4 vertices
                u1, v1,
                u1, v2,
                u2, v2,
                u2, v1,

                // Bottom face  4 vertices
                u1, v1,
                u1, v2,
                u2, v2,
                u2, v1

        };
    }

    public float[] getAllTextureCoords() {
        List<float[]> allCoords = new ArrayList<>();
        for (int i = 0; i < textureRectangles.size(); i++) {
            allCoords.add(getTextureCoords(i));
        }

        float[] result = new float[allCoords.size() * 32];
        for(int i = 0; i < allCoords.size(); i++) {
            System.arraycopy(allCoords.get(i), 0, result, i * 32, 32);
        }

        return result;
    }

    public static TextureAtlas getInstance() {
        if (instance == null) {
            instance = new TextureAtlas();
        }
        return instance;
    }
}