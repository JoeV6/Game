package org.lpc.handler;

import lombok.Setter;
import org.lpc.Game;
import org.lpc.render.Camera;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;

import static org.lpc.Game.DEFAULT_HEIGHT;
import static org.lpc.Game.DEFAULT_WIDTH;

public class InputHandler {
    private final Game game;
    private final long window;
    private final Camera camera;

    private float scrollVelocity = 0.2f;

    @Setter
    private float mouseX, mouseY;

    public InputHandler() {
        System.out.println(Game.getInstance());
        this.game = Game.getInstance();
        this.window = game.getWindow();
        this.camera = game.getCamera();
    }

    public void processInput() {

    }

    private void toggleFullscreen() {
        long monitor = GLFW.glfwGetPrimaryMonitor();
        GLFWVidMode videoMode = GLFW.glfwGetVideoMode(monitor);

        assert videoMode != null;
        if (game.isFullscreen()) {
            GLFW.glfwSetWindowMonitor(window, 0, (videoMode.width() - DEFAULT_WIDTH) / 2, (videoMode.height() - DEFAULT_HEIGHT) / 2, DEFAULT_WIDTH, DEFAULT_HEIGHT, videoMode.refreshRate());
        } else {
            GLFW.glfwSetWindowMonitor(window, monitor, 0, 0, videoMode.width(), videoMode.height(), videoMode.refreshRate());
        }

        game.setFullscreen(!game.isFullscreen());
    }

    public void mouseInput(int button, int action) {
        float x = mouseX;
        float y = mouseY;

        if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT && action == GLFW.GLFW_PRESS) {
            System.out.println("Mouse input: " + x + ", " + y);
        }
        if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT && action == GLFW.GLFW_PRESS) {
            System.out.println("Mouse input: " + x + ", " + y);
        }
    }

    public void scrollInput(double xoffset, double yoffset) {
        System.out.println("Scroll input: " + xoffset + ", " + yoffset);
    }

    public void keyInput(int key, int scancode, int action, int mods) {
        if(key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_PRESS) {
            GLFW.glfwSetWindowShouldClose(window, true);
        }
        if(key == GLFW.GLFW_KEY_F11 && action == GLFW.GLFW_PRESS) {
            toggleFullscreen();
        }

        if(key == GLFW.GLFW_KEY_W && action == GLFW.GLFW_PRESS) {
            System.out.println("w");
            camera.move(0, 0, -0.5f);
        }
        if(key == GLFW.GLFW_KEY_S && action == GLFW.GLFW_PRESS) {
            System.out.println("s");
            camera.move(0, 0, 0.5f);
        }
        if(key == GLFW.GLFW_KEY_D && action == GLFW.GLFW_PRESS) {
            System.out.println("d");
            camera.move(0.5f, 0, 0);
        }
        if(key == GLFW.GLFW_KEY_A && action == GLFW.GLFW_PRESS) {
            System.out.println("d");
            camera.move(-0.5f, 0, 0);
        }
        if(key == GLFW.GLFW_KEY_Q && action == GLFW.GLFW_PRESS) {
            System.out.println("d");
            camera.rotate(0, -5f, 0);
        }
        if(key == GLFW.GLFW_KEY_E && action == GLFW.GLFW_PRESS) {
            System.out.println("d");
            camera.rotate(0, 5f, 0);
        }

    }
}
