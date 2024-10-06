package org.lpc.handler;

import org.lpc.Game;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;

import static org.lpc.Game.DEFAULT_HEIGHT;
import static org.lpc.Game.DEFAULT_WIDTH;

public class InputHandler {
    private final Game main;
    private final long window;

    private boolean
            wasEscPressed,
            wasF11Pressed = false;

    public InputHandler() {
        System.out.println(Game.getInstance());
        this.main = Game.getInstance();
        this.window = main.getWindow();
    }

    public void processInput() {
        boolean escPressed = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_ESCAPE) == GLFW.GLFW_PRESS;
        if (escPressed && !wasEscPressed) {
            GLFW.glfwSetWindowShouldClose(window, true);
        }
        wasEscPressed = escPressed;

        boolean f11Pressed = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_F11) == GLFW.GLFW_PRESS;
        if (f11Pressed && !wasF11Pressed) {
            toggleFullscreen();
        }
        wasF11Pressed = f11Pressed;
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

}
