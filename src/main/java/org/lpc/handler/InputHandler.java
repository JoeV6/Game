package org.lpc.handler;

import lombok.Setter;
import org.lpc.Game;
import org.lpc.render.Camera;
import org.lpc.world.World;
import org.lpc.world.block.AbstractBlock;
import org.lpc.world.entity.entities.PlayerEntity;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;

import java.util.HashMap;

import static org.lpc.Game.*;

public class InputHandler {
    private static final boolean DEBUG = true;

    private final long window;
    private final Game game;
    private final Camera camera;
    private final PlayerEntity player;

    private final HashMap<Integer, Boolean> keys = new HashMap<>();

    @Setter
    private float mouseX, mouseY;

    public InputHandler() {
        this.game = Game.getInstance();
        this.window = game.getWindow();
        this.camera = game.getCamera();
        this.player = game.getPlayer();

        //init all keys
        for(int i = 0; i < 400; i++){
            keys.put(i, false);
        }
    }

    public void processInput() {
        playerMovement();
    }

    private void playerMovement() {
        if (keys.get(GLFW.GLFW_KEY_W)) {
            player.moveForward(DEFAULT_MOVEMENT_SPEED);
            if (keys.get(GLFW.GLFW_KEY_LEFT_CONTROL)) {
                player.moveForward(DEFAULT_MOVEMENT_SPEED * 5);
            }
        }
        if (keys.get(GLFW.GLFW_KEY_S)) {
            player.moveForward(-DEFAULT_MOVEMENT_SPEED);
        }
        if (keys.get(GLFW.GLFW_KEY_D)) {
            player.moveLeft(-DEFAULT_MOVEMENT_SPEED);
        }
        if (keys.get(GLFW.GLFW_KEY_A)) {
            player.moveLeft(DEFAULT_MOVEMENT_SPEED);
        }

        if (keys.get(GLFW.GLFW_KEY_SPACE)) {
            player.move(0, DEFAULT_MOVEMENT_SPEED, 0);
            if (keys.get(GLFW.GLFW_KEY_LEFT_CONTROL)) {
                player.move(0, DEFAULT_MOVEMENT_SPEED * 2, 0);
            }
        }
        if (keys.get(GLFW.GLFW_KEY_LEFT_SHIFT)) {
            player.move(0, -DEFAULT_MOVEMENT_SPEED, 0);
            if (keys.get(GLFW.GLFW_KEY_LEFT_CONTROL)) {
                player.move(0, -DEFAULT_MOVEMENT_SPEED * 2, 0);
            }
        }
    }

    public void keyInput(int key, int scancode, int action, int mods) {
        if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_PRESS) {
            GLFW.glfwSetWindowShouldClose(window, true);
        }
        if (key == GLFW.GLFW_KEY_F11 && action == GLFW.GLFW_PRESS) {
            toggleFullscreen();
        }


        if (key == GLFW.GLFW_KEY_1 && action == GLFW.GLFW_PRESS && DEBUG) {
            game.getUpdateHandler().toggleRenderAll();
        }
        if (key == GLFW.GLFW_KEY_2 && action == GLFW.GLFW_PRESS && DEBUG) {
            game.changeRenderType();
        }


        if(keys.containsKey(key) && (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_RELEASE)) {
            if(action == 1){
                keys.put(key, true);
            } else {
                keys.put(key, false);
            }
        }
    }

    private void toggleFullscreen() {
        long monitor = GLFW.glfwGetPrimaryMonitor();
        GLFWVidMode videoMode = GLFW.glfwGetVideoMode(monitor);

        assert videoMode != null;
        if (game.isFullscreen()) {
            GLFW.glfwSetWindowMonitor(window, 0, (videoMode.width() - DEFAULT_WIDTH) / 2, (videoMode.height() - DEFAULT_HEIGHT) / 2, DEFAULT_WIDTH, DEFAULT_HEIGHT, videoMode.refreshRate());
        } else {
            GLFW.glfwSetWindowMonitor(window, 0, 0, 0, videoMode.width(), videoMode.height(), videoMode.refreshRate());
        }

        game.setFullscreen(!game.isFullscreen());
    }

    public void mouseInput(int button, int action) {
        float x = mouseX;
        float y = mouseY;

        if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT && action == GLFW.GLFW_PRESS) {
            System.out.println("Mouse input: " + x + ", " + y);
            World world = game.getWorld();
            PlayerEntity player = game.getPlayer();

            AbstractBlock b = player.getFirstBlockInFront(10, 0.1f);
            if(b == null) return;

            world.removeBlock(b);
        }
        if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT && action == GLFW.GLFW_PRESS) {
            System.out.println("Mouse input: " + x + ", " + y);
        }
    }

    public void mouseMovement(float xOffset, float yOffset) {
        float sensitivity = 0.08f; // Adjust this for sensitivity
        xOffset *= sensitivity;
        yOffset *= sensitivity;

        camera.rotate(xOffset, yOffset, 0);

        if (camera.getPitch() > 89.0f) {
            camera.setPitch(89.0f);
        } else if (camera.getPitch() < -89.0f) {
            camera.setPitch(-89.0f);
        }
    }

    public void scrollInput(double xoffset, double yoffset) {
        System.out.println("Scroll input: " + xoffset + ", " + yoffset);
    }
}
