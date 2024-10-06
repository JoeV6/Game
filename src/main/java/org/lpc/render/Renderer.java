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
    private final long window;
    private final TextureHandler textureHandler;

    public Renderer() {
        game = Game.getInstance();
        window = game.getWindow();
        textureHandler = game.getTextureHandler();

        initTextures();
    }

    public void initTextures() {
        textureHandler.loadTexture(textureHandler.nextId(), "src/main/resources/textures/texture_grass_tile.png");
    }

    public void renderGame() {
        clearScreen(); // Ensure the screen is cleared before rendering

        renderTexture(textureHandler.getTexture(0)); // Render the texture
    }

    private void renderTexture(Texture texture) {
        texture.bind(); // Ensure the texture is bound before rendering


        glBegin(GL_QUADS); // Begin drawing a quad
            glTexCoord2f(0, 0); glVertex2f(-0.5f, -0.5f);
            glTexCoord2f(1, 0); glVertex2f(0.5f, -0.5f);
            glTexCoord2f(1, 1); glVertex2f(0.5f, 0.5f);
            glTexCoord2f(0, 1); glVertex2f(-0.5f, 0.5f);
        glEnd(); // End drawing the quad

    }

    private void clearScreen() {
        glClearColor(0.1f, 0.2f, 0.5f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }
}
