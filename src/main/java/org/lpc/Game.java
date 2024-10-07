package org.lpc;

import lombok.Getter;
import lombok.Setter;
import org.lpc.handler.InputHandler;
import org.lpc.handler.UpdateHandler;
import org.lpc.render.Camera;
import org.lpc.render.Renderer;
import org.lpc.render.pipeline.ModelLoader;
import org.lpc.render.pipeline.models.FullModel;
import org.lpc.render.pipeline.shaders.StaticShader;
import org.lpc.world.World;
import org.lpc.world.block.AbstractBlock;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;


@Getter
public class Game {
    private static Game instance = null;

    public static final int DEFAULT_WIDTH = 1080;
    public static final int DEFAULT_HEIGHT = 720;
    public static final double UPDATES_PER_SECOND = 60.0;

    private long window;
    @Setter boolean fullscreen;

    private InputHandler inputHandler;
    private UpdateHandler updateHandler;
    private Renderer renderer;

    private World world;

    private StaticShader shader;
    private Camera camera;
    private ModelLoader modelLoader;

    private final ArrayList<FullModel> renderModels = new ArrayList<>();

    public void run() {
        System.out.println("LWJGL " + Version.getVersion());

        initGLFW();
        initGameLoop();
        exitGracefully();
    }

    private void initGLFW() {
        GLFWErrorCallback.createPrint(System.err).set();

        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        window = glfwCreateWindow(DEFAULT_WIDTH, DEFAULT_HEIGHT, "Game", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");


        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        centerWindow(window);
        setCallBacks();
    }

    private void initGameLoop() {
        GL.createCapabilities();

        initClasses();

        glfwShowWindow(window);

        double previousTime = System.nanoTime();
        double lag = 0.0;

        while (!GLFW.glfwWindowShouldClose(window)) {
            double currentTime = System.nanoTime();
            double elapsedTime = currentTime - previousTime;
            previousTime = currentTime;
            lag += elapsedTime;

            renderer.prepare();
            inputHandler.processInput();

            GLFW.glfwPollEvents();

            // Update game logic with fixed time-step
            double nanosecondsPerUpdate = 1_000_000_000.0 / UPDATES_PER_SECOND;

            while (lag >= nanosecondsPerUpdate) {
                updateHandler.update();
                lag -= nanosecondsPerUpdate;
            }

            shader.start();
            for (FullModel fullModel : renderModels) {
                renderer.render(fullModel, shader);
            }
            shader.stop();

            // Swap buffers and poll for events (input)
            GLFW.glfwSwapBuffers(window);
        }
    }

    private void exitGracefully() {
        shader.cleanUp();

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }

    private void initClasses() {
        modelLoader = new ModelLoader();
        shader = new StaticShader();
        camera = new Camera();
        renderer = new Renderer(shader, camera);

        world = new World(20, 20, 20);


        for(AbstractBlock[][] block : world.getBlocks()) {
            for(AbstractBlock[] block1 : block) {
                for(AbstractBlock block2 : block1) {
                    if (block2 == null) {
                        continue;
                    }
                    renderModels.add(block2.getCubeModel().getModel());
                }
            }
        }

        inputHandler = new InputHandler();
        updateHandler = new UpdateHandler();
    }

    private void setCallBacks(){
        glfwSetCursorPosCallback(window, (window, xpos, ypos) -> {
            inputHandler.setMouseX((float) xpos);
            inputHandler.setMouseY((float) ypos);
        });
        glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
            inputHandler.mouseInput(button, action);
        });
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            inputHandler.keyInput(key, scancode, action, mods);
        });
        glfwSetScrollCallback(window, (window, xoffset, yoffset) -> {
            inputHandler.scrollInput(xoffset, yoffset);
        });
        glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
            glViewport(0, 0, width, height);
        });

        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        glfwSetCursorPosCallback(window, new GLFWCursorPosCallback() {
            private double lastX = 0, lastY = 0;
            private boolean firstMouse = true; // ignore the initial jump

            @Override
            public void invoke(long window, double xpos, double ypos) {
                if (firstMouse) { // Initially set the lastX, lastY to the first cursor position
                    lastX = xpos;
                    lastY = ypos;
                    firstMouse = false;
                }

                float xOffset = (float) (xpos - lastX);
                float yOffset = (float) (ypos - lastY);

                lastX = xpos;
                lastY = ypos;

                inputHandler.mouseMovement(xOffset, yOffset);
            }
        });
    }

    private void centerWindow(long window) {
        int[] windowSize = getCurrentWindowSize();
        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (vidMode != null) {
            glfwSetWindowPos(
                    window,
                    (vidMode.width() - windowSize[0]) / 2,
                    (vidMode.height() - windowSize[1]) / 2
            );
        }
    }

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

