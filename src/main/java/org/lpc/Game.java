package org.lpc;

import lombok.Getter;
import lombok.Setter;
import org.lpc.handler.InputHandler;
import org.lpc.handler.RenderHandler;
import org.lpc.handler.UpdateHandler;
import org.lpc.render.textures.TextureHandler;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;


public class Game {
    private static Game instance = null;


    public static final int DEFAULT_WIDTH = 800;
    public static final int DEFAULT_HEIGHT = 600;

    private final double UPDATES_PER_SECOND = 60.0;
    private final double NANOSEC_PER_UPDATE = 1_000_000_000.0 / UPDATES_PER_SECOND;

    @Getter private long window;
    @Getter @Setter boolean fullscreen;

    private double lastTime;
    private double deltaTime;

    @Getter private InputHandler inputHandler;
    @Getter private UpdateHandler updateHandler;
    @Getter private TextureHandler textureHandler;
    @Getter private RenderHandler renderHandler;

    private Game() {
        window = 0;
        fullscreen = false;
        lastTime = 0;
        deltaTime = 0;
    }

    public static Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    public void run() {
        System.out.println("LWJGL " + Version.getVersion());

        init();
        loop();
        exitGracefully();
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);


        window = glfwCreateWindow(800, 500, "Game", NULL, NULL);
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
    }

    private void loop() {
        GL.createCapabilities();

        initHandlers();

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
            while (lag >= NANOSEC_PER_UPDATE) {
                updateHandler.update();
                lag -= NANOSEC_PER_UPDATE;
            }

            // Render the frame (optional interpolation)
            renderHandler.render();

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

    private void initHandlers() {
        inputHandler = new InputHandler();
        updateHandler = new UpdateHandler();
        textureHandler = new TextureHandler();
        renderHandler = new RenderHandler();
    }
}

