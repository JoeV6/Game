package org.lpc.handler;

import lombok.Setter;
import org.lpc.Game;
import org.lpc.render.Camera;
import org.lpc.world.World;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;

import java.util.HashMap;

import static org.lpc.Game.DEFAULT_HEIGHT;
import static org.lpc.Game.DEFAULT_WIDTH;

public class InputHandler {
    private final Game game;
    private final long window;
    private final Camera camera;

    private float scrollVelocity = 0.2f;

    private HashMap<Integer, Boolean> keys = new HashMap<>();

    @Setter
    private float mouseX, mouseY;

    public InputHandler() {
        System.out.println(Game.getInstance());
        this.game = Game.getInstance();
        this.window = game.getWindow();
        this.camera = game.getCamera();

        keys.put(GLFW.GLFW_KEY_W, false);
        keys.put(GLFW.GLFW_KEY_A, false);
        keys.put(GLFW.GLFW_KEY_S, false);
        keys.put(GLFW.GLFW_KEY_D, false);

        keys.put(GLFW.GLFW_KEY_SPACE, false);
        keys.put(GLFW.GLFW_KEY_LEFT_SHIFT, false);

        keys.put(GLFW.GLFW_KEY_Q, false);
        keys.put(GLFW.GLFW_KEY_E, false);
    }

    public void processInput() {
        if(keys.get(GLFW.GLFW_KEY_W)) {
            camera.moveForward(0.1f);
        }
        if(keys.get(GLFW.GLFW_KEY_S)) {
            camera.moveForward(-0.1f);
        }
        if(keys.get(GLFW.GLFW_KEY_D)) {
            camera.moveLeft(-0.1f);
        }
        if(keys.get(GLFW.GLFW_KEY_A)) {
            camera.moveLeft(0.1f);
        }

        if(keys.get(GLFW.GLFW_KEY_SPACE)){
            camera.move(0, 0.1f, 0);
        }
        if(keys.get(GLFW.GLFW_KEY_LEFT_SHIFT)){
            camera.move(0, -0.1f, 0);
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

    public void keyInput(int key, int scancode, int action, int mods) {
        if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_PRESS) {
            GLFW.glfwSetWindowShouldClose(window, true);
        }
        if (key == GLFW.GLFW_KEY_F11 && action == GLFW.GLFW_PRESS) {
            toggleFullscreen();
        }

        if(keys.containsKey(key) && (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_RELEASE)) {
            if(action == 1){
                keys.put(key, true);
            } else {
                keys.put(key, false);
            }
        }
    }
}
