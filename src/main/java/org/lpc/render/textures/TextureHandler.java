package org.lpc.render.textures;

import lombok.Getter;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class TextureHandler {
    @Getter private final File texturesDir = new File("src/main/resources/textures/tiles");

    private final Map<Integer, Texture> textures = new HashMap<>();

    public TextureHandler() {
        initTextures();
    }

    private void initTextures() {
        File texturesDir = getTexturesDir();

        if (!texturesDir.exists()) {
            System.err.println("Textures directory not found: " + texturesDir.getAbsolutePath());
            return;
        }

        File[] textureFiles = texturesDir.listFiles((dir, name) ->
                name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg")
        );

        if (textureFiles == null || textureFiles.length == 0) {
            System.err.println("No texture files found in the directory: " + texturesDir.getAbsolutePath());
            return;
        }

        Arrays.sort(textureFiles, this::compareTextureFiles);

        for (int i = 0; i < textureFiles.length; i++) {
            loadTexture(i, textureFiles[i].getPath());
        }
    }

    private int compareTextureFiles(File f1, File f2) {
        Integer tileNumber1 = extractTileNumber(f1);
        Integer tileNumber2 = extractTileNumber(f2);

        // Compare based on tile numbers, or by name if not tile files
        if (tileNumber1 != null && tileNumber2 != null) {
            return tileNumber1 - tileNumber2;
        } else {
            return f1.getName().compareTo(f2.getName());
        }
    }

    private Integer extractTileNumber(File file) {
        String fileName = file.getName();
        if (fileName.startsWith("tile_")) {
            try {
                // Extract the number after "tile_"
                return Integer.parseInt(fileName.substring(5, fileName.lastIndexOf('.')));
            } catch (NumberFormatException e) {
                System.err.println("Invalid tile number in filename: " + fileName);
            }
        }
        return null;
    }

    public void loadTexture(int id, String path) {
        textures.put(id, new Texture(path));
    }

    public Texture getTexture(int id) {
        return textures.get(id);
    }

    public int getTextureId(String path) {
        // this will take a while if there are many textures
        // consider using a different data structure for faster lookup

        for (Map.Entry<Integer, Texture> entry : textures.entrySet()) {
            if (Objects.equals(entry.getValue().getFilepath(), path)) {
                return entry.getKey();
            }
        }
        return -1;
    }
}

