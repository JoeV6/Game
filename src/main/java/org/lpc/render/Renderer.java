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
    public static final int MIN_TILE_SIZE = 32;
    public static int TILESIZE = 32;

    private final TextureHandler textureHandler;
    private final World world;

    public Renderer() {
        Game game = Game.getInstance();
        textureHandler = game.getTextureHandler();
        world = game.getWorld();
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
        glClearColor(0.10f, 0.12f, 0.15f, 1.0f);
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
    public static float[] convertToOpenglCoordinates(AbstractTile tile) {
        Game game = Game.getInstance();
        World world = game.getWorld();

        int windowWidth = game.getCurrentWindowSize()[0];
        int windowHeight = game.getCurrentWindowSize()[1];

        int numTilesX = world.getWidth();
        int numTilesY = world.getHeight();

        float pixelWidth = TILESIZE;
        float pixelHeight = TILESIZE;

        float pixelX = tile.getX() * TILESIZE;
        float pixelY = tile.getY() * TILESIZE;


        if (pixelX + pixelWidth < 0 || pixelX > windowWidth ||
                pixelY + pixelHeight < 0 || pixelY > windowHeight) {
            // Tile is out of window bounds, don't render it
            return null; // Return null to indicate the tile should not be rendered
        }

        float ndcX = (2 * pixelX) / windowWidth - 1;
        float ndcY = 1 - (2 * pixelY) / windowHeight;


        float ndcWidth = (2 * pixelWidth) / windowWidth;
        float ndcHeight = (2 * pixelHeight) / windowHeight;

        if (numTilesX * TILESIZE < windowWidth) {
            float diff = (float) (windowWidth - numTilesX * TILESIZE) / windowWidth;
            ndcX += diff;
        }
        if (numTilesY * TILESIZE < windowHeight) {
            float diff = (float) (windowHeight - numTilesY * TILESIZE) / windowHeight;
            ndcY -= diff;
        }

        return new float[]{ndcX, ndcY, ndcWidth, ndcHeight};
    }

    /**
     * Converts the mouse coordinates to tile coordinates.
     * @param mouseX The x-coordinate of the mouse.
     * @param mouseY The y-coordinate of the mouse.
     * @return An integer array containing the x and y tile coordinates
     *         or null if the mouse is outside the map bounds.
     */
    public static int[] convertMouseToTileCoordinates(float mouseX, float mouseY) {
        Game game = Game.getInstance();
        World world = game.getWorld();

        int windowWidth = game.getCurrentWindowSize()[0];
        int windowHeight = game.getCurrentWindowSize()[1];

        int numTilesX = world.getWidth();
        int numTilesY = world.getHeight();

        int mapWidth = numTilesX * TILESIZE;
        int mapHeight = numTilesY * TILESIZE;


        if (mouseX < (float) (windowWidth - mapWidth) / 2 || mouseX > (float) (windowWidth + mapWidth) / 2 ||
                mouseY < (float) (windowHeight - mapHeight) / 2 || mouseY > (float) (windowHeight + mapHeight) / 2) {
            // Mouse is outside the map bounds, return null
            return null;
        }

        int tileX = (int) ((mouseX - (float) (windowWidth - mapWidth) / 2) / TILESIZE);
        int tileY = (int) ((mouseY - (float) (windowHeight - mapHeight) / 2) / TILESIZE);


        return new int[]{tileX, tileY};
    }
}

