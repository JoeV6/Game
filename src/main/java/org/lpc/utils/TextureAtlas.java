package org.lpc.utils;

import lombok.Getter;
import org.lpc.render.pipeline.models.CubeModel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

@Getter
public class TextureAtlas {
    public static final int TEXTURE_SIZE = 64;

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
                BufferedImage resizedImage = resizeImage(originalImage, TEXTURE_SIZE, TEXTURE_SIZE);
                images.add(resizedImage);
            } catch (IOException e) {
                System.out.println("Failed to load image: " + file.getName());
            }
        }

        int atlasWidth = TEXTURE_SIZE;
        int atlasHeight = (images.size() * TEXTURE_SIZE);

        atlasImage = new BufferedImage(atlasWidth, atlasHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = atlasImage.createGraphics();

        // Add images to the atlas and store coordinates
        int currentY = 0;
        for (BufferedImage image : images) {
            g.drawImage(image, 0, currentY, null);
            Rectangle rect = new Rectangle(0, currentY, TEXTURE_SIZE, TEXTURE_SIZE);
            textureRectangles.add(rect);
            textureCoordinates.put(image.toString(), rect);
            currentY += TEXTURE_SIZE; // Move down by 64 pixels
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

    public float[] getAllTextureCoords() {
        return CubeModel.textureCoords;
    }

    public static TextureAtlas getInstance() {
        if (instance == null) {
            instance = new TextureAtlas();
        }
        return instance;
    }
}