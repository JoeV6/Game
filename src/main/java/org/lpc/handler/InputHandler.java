package org.lpc.handler;

import lombok.Setter;
import org.lpc.Game;
import org.lpc.render.Renderer;
import org.lpc.world.World;
import org.lpc.world.tiles.FloorTile;
import org.lpc.world.tiles.floor_tiles.GrassTile;
import org.lpc.world.tiles.wall_tiles.StoneWallTile;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;

import static org.lpc.Game.DEFAULT_HEIGHT;
import static org.lpc.Game.DEFAULT_WIDTH;

public class InputHandler {
    private final Game main;
    private final long window;

    private float scrollVelocity = 0.2f;

    @Setter
    private float mouseX, mouseY;

    public InputHandler() {
        System.out.println(Game.getInstance());
        this.main = Game.getInstance();
        this.window = main.getWindow();
    }

    public void processInput() {

    }

    private void toggleFullscreen() {
        long monitor = GLFW.glfwGetPrimaryMonitor();
        GLFWVidMode videoMode = GLFW.glfwGetVideoMode(monitor);

        assert videoMode != null;
        if (main.isFullscreen()) {
            GLFW.glfwSetWindowMonitor(window, 0, (videoMode.width() - DEFAULT_WIDTH) / 2, (videoMode.height() - DEFAULT_HEIGHT) / 2, DEFAULT_WIDTH, DEFAULT_HEIGHT, videoMode.refreshRate());
        } else {
            GLFW.glfwSetWindowMonitor(window, monitor, 0, 0, videoMode.width(), videoMode.height(), videoMode.refreshRate());
        }

        main.setFullscreen(!main.isFullscreen());
    }

    public void mouseInput(int button, int action) {
        float x = mouseX;
        float y = mouseY;

        if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT && action == GLFW.GLFW_PRESS) {
            System.out.println("Mouse left click at: " + x + ", " + y);
            World world = main.getWorld();
            int[] tileCoordinates = Renderer.convertMouseToTileCoordinates(x, y);

            if (tileCoordinates != null) {
                world.setTile(tileCoordinates[0], tileCoordinates[1], new StoneWallTile(tileCoordinates[0], tileCoordinates[1]));
            }
        }
        if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT && action == GLFW.GLFW_PRESS) {
            System.out.println("Mouse right click at: " + x + ", " + y);
            World world = main.getWorld();
            int[] tileCoordinates = Renderer.convertMouseToTileCoordinates(x, y);

            if (tileCoordinates != null) {
                world.setTile(tileCoordinates[0], tileCoordinates[1], new GrassTile(tileCoordinates[0], tileCoordinates[1]));
            }
        }
    }

    public void scrollInput(double xoffset, double yoffset) {
        System.out.println("Scroll input: " + xoffset + ", " + yoffset);

        if (Renderer.TILESIZE + (int) (yoffset * scrollVelocity * 20) < Renderer.MIN_TILE_SIZE) {
            return;
        }
        Renderer.TILESIZE += (int) (yoffset * scrollVelocity * 20);
    }

    public void keyInput(int key, int scancode, int action, int mods) {
        if(key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_PRESS) {
            GLFW.glfwSetWindowShouldClose(window, true);
        }
        if(key == GLFW.GLFW_KEY_F11 && action == GLFW.GLFW_PRESS) {
            toggleFullscreen();
        }
    }
}
