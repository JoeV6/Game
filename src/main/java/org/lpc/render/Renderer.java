// src/main/java/org/lpc/render/Renderer.java
package org.lpc.render;

import org.lpc.Game;
import org.lpc.render.textures.Texture;
import org.lpc.render.textures.TextureHandler;
import org.lpc.world.World;
import org.lpc.world.tiles.AbstractTile;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11C.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_QUADS;

/**
 * The Renderer class is responsible for rendering the game.
 * coordinate system for OpenGL is (-1, -1) in the bottom left corner, and (1, 1) in the top right corner.
 * coordinate system for the game is (0, 0) in the top left corner, and (worldwidth, worldheight) in the bottom right corner.
 */

public class Renderer {
    public static final int TILESIZE = 32;

    private final Game game;
    private final TextureHandler textureHandler;
    private final World world;

    public Renderer() {
        game = Game.getInstance();
        textureHandler = game.getTextureHandler();
        world = game.getWorld();

        initTextures();
    }

    public void initTextures() {
        textureHandler.loadTexture(0, "src/main/resources/textures/tile_0.png");
        textureHandler.loadTexture(1, "src/main/resources/textures/tile_1.png");
    }

    public void renderGame() {
        clearScreen();

        for (int i = 0; i < world.getWidth(); i++) {
            for (int j = 0; j < world.getHeight(); j++) {
                AbstractTile tile = world.getTileAt(i, j);
                if (tile != null) {
                    renderTile(tile);
                }
            }
        }
    }

    public void renderTile(AbstractTile tile) {
        Texture texture = textureHandler.getTexture(tile.getTextureID());

        float[] openglCoordinates = convertToOpenglCoordinates(tile);

        if (openglCoordinates == null) return; // Don't render the tile if it's out of bounds

        renderTexture(texture, openglCoordinates[0], openglCoordinates[1], openglCoordinates[2], openglCoordinates[3]);
    }

    private void clearScreen() {
        glClearColor(0.1f, 0.2f, 0.5f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    private void renderTexture(Texture texture, float x, float y, float width, float height) {
        texture.bind(); // Bind the texture

        glEnable(GL_TEXTURE_2D);

        glBegin(GL_QUADS);
        glTexCoord2f(0, 0); glVertex2f(x, y); // Top-left corner
        glTexCoord2f(1, 0); glVertex2f(x + width, y); // Top-right corner
        glTexCoord2f(1, 1); glVertex2f(x + width, y - height); // Bottom-right corner (Y is flipped)
        glTexCoord2f(0, 1); glVertex2f(x, y - height); // Bottom-left corner (Y is flipped)
        glEnd();
    }

    /**
     * Converts the tile's world coordinates and size to OpenGL's normalized device coordinates (-1 to 1).
     * @param tile The tile to convert.
     * @return A float array containing the x, y, width, and height in OpenGL coordinates.
     */
    private float[] convertToOpenglCoordinates(AbstractTile tile) {
        int windowWidth = game.getCurrentWindowSize()[0];  // Total screen width in pixels
        int windowHeight = game.getCurrentWindowSize()[1]; // Total screen height in pixels

        // Calculate the number of tiles horizontally and vertically based on the world dimensions
        int numTilesX = world.getWidth();
        int numTilesY = world.getHeight();

        // Fixed tile size in pixels
        float pixelWidth = TILESIZE;  // Each tile is always 32 pixels wide
        float pixelHeight = TILESIZE; // Each tile is always 32 pixels tall

        // Calculate the pixel coordinates of the tile
        float pixelX = tile.getX() * TILESIZE; // X position
        float pixelY = tile.getY() * TILESIZE; // Y position

        // Check if the tile is completely within the window bounds
        if (pixelX + pixelWidth < 0 || pixelX > windowWidth ||
                pixelY + pixelHeight < 0 || pixelY > windowHeight) {
            // Tile is out of window bounds, don't render it
            return null; // Return null to indicate the tile should not be rendered
        }

        // Calculate the normalized device coordinates (NDC) of the tile
        float ndcX = (2 * pixelX) / windowWidth - 1; // X NDC
        float ndcY = 1 - (2 * pixelY) / windowHeight; // Y NDC

        // convert 32 pixels to NDC
        float ndcWidth = (2 * pixelWidth) / windowWidth; // Width NDC
        float ndcHeight = (2 * pixelHeight) / windowHeight; // Height NDC

        return new float[]{ndcX, ndcY, ndcWidth, ndcHeight}; // Return NDC values
    }
}

