// src/main/java/org/lpc/render/Renderer.java
package org.lpc.render;

import org.lpc.Game;
import org.lpc.render.textures.Texture;
import org.lpc.render.textures.TextureHandler;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11C.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_QUADS;

public class Renderer {
    private final Game game;
    private final TextureHandler textureHandler;

    public Renderer() {
        game = Game.getInstance();
        textureHandler = game.getTextureHandler();

        initTextures();
    }

    public void initTextures() {
        textureHandler.loadTexture(0, "src/main/resources/textures/texture_grass_tile.png");
    }

    public void renderGame() {
        clearScreen();

        renderTexture(textureHandler.getTexture(0), -1, -1, 1, 1);
    }

    private void renderTexture(Texture texture, float x, float y, float width, float height) {
        texture.bind();  // Bind the texture

        glEnable(GL_TEXTURE_2D);  // Enable 2D texturing

        glBegin(GL_QUADS);
        glTexCoord2f(0, 0); glVertex2f(x, y);
        glTexCoord2f(1, 0); glVertex2f(x + width, y);
        glTexCoord2f(1, 1); glVertex2f(x + width, y + height);
        glTexCoord2f(0, 1); glVertex2f(x, y + height);
        glEnd();
    }

    private void clearScreen() {
        glClearColor(0.1f, 0.2f, 0.5f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }
}
