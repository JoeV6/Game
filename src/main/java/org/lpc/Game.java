package org.lpc;

import lombok.Getter;
import lombok.Setter;
import org.lpc.handler.InputHandler;
import org.lpc.handler.UpdateHandler;
import org.lpc.render.Renderer;
import org.lpc.render.textures.TextureHandler;
import org.lpc.world.World;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;


@Getter
public class Game {
    private static Game instance = null;

    public static final int DEFAULT_WIDTH = 1280;
    public static final int DEFAULT_HEIGHT = 640;
    public static final double UPDATES_PER_SECOND = 60.0;

    private long window;
    @Setter boolean fullscreen;
    private float mouseX, mouseY;

    private InputHandler inputHandler;
    private UpdateHandler updateHandler;
    private TextureHandler textureHandler;
    private Renderer renderer;

    private World world;

    private Game() {
        window = 0;
        fullscreen = false;
    }

    public static Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    public void run() {
        System.out.println("LWJGL " + Version.getVersion());

        initGLFW();
        initGame();
        initGameLoop();
        exitGracefully();
    }

    private void initGLFW() {
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);


        window = glfwCreateWindow(DEFAULT_WIDTH, DEFAULT_HEIGHT, "Game", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        });

        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            glfwGetWindowSize(window, pWidth, pHeight);
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);

        // Set the mouse position callback
        glfwSetCursorPosCallback(window, (window, xpos, ypos) -> {
            mouseX = (float) xpos;
            mouseY = (float) ypos;
        });
        // Set the mouse button callback
        glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
            if (button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS) {
                inputHandler.mouseInput(GLFW_MOUSE_BUTTON_1, GLFW_PRESS, mouseX, mouseY);
            }
        });
        // Set key input callback
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            inputHandler.keyInput(key, scancode, action, mods);
        });

    }

    private void initGame() {
        world = new World(DEFAULT_WIDTH / Renderer.TILESIZE, DEFAULT_HEIGHT / Renderer.TILESIZE);
    }

    private void initGameLoop() {
        GL.createCapabilities();

        initClasses();

        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

        double previousTime = System.nanoTime();
        double lag = 0.0;

        while (!GLFW.glfwWindowShouldClose(window)) {
            double currentTime = System.nanoTime();
            double elapsedTime = currentTime - previousTime;
            previousTime = currentTime;
            lag += elapsedTime;

            inputHandler.processInput();

            // Update game logic with fixed time-step
            double nanosecondsPerUpdate = 1_000_000_000.0 / UPDATES_PER_SECOND;

            while (lag >= nanosecondsPerUpdate) {
                updateHandler.update();
                lag -= nanosecondsPerUpdate;
            }

            // Render the frame (optional interpolation)
            renderer.renderGame();

            // Swap buffers and poll for events (input)
            GLFW.glfwSwapBuffers(window);
            GLFW.glfwPollEvents();
        }
    }

    private void exitGracefully() {
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void initClasses() {
        inputHandler = new InputHandler();
        updateHandler = new UpdateHandler();
        textureHandler = new TextureHandler();
        renderer = new Renderer();
    }

    /**
     * Get the current screen size
     * @return int[] containing the width [0] and height [1] of the screen
     */
    public int[] getCurrentWindowSize() {
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(window, pWidth, pHeight);

            return new int[]{pWidth.get(0), pHeight.get(0)};
        }
    }
}

