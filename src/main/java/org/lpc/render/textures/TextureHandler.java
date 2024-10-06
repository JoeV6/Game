package org.lpc.render.textures;

import java.util.HashMap;
import java.util.Map;


public class TextureHandler {
    private final Map<Integer, Texture> textures = new HashMap<>();

    public void loadTexture(int id, String path) {
        textures.put(id, new Texture(path));
    }

    public Texture getTexture(int id) {
        return textures.get(id);
    }

    public int nextId() {
        return textures.size();
    }
}

