package org.lpc;

import lombok.Getter;
import lombok.Setter;
import org.lpc.handler.InputHandler;
import org.lpc.handler.UpdateHandler;
import org.lpc.render.Camera;
import org.lpc.render.pipeline.shaders.StaticShader;
import org.lpc.render.pipeline.Renderer;
import org.lpc.render.pipeline.models.CubeModel;
import org.lpc.utils.TextureAtlas;
import org.lpc.world.World;
import org.lpc.world.chunk.Chunk;
import org.lpc.world.entity.entities.PlayerEntity;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.awt.*;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;


@Getter
public class Game {
    private static Game instance = null;

    public static final int DEFAULT_WIDTH = 1080;
    public static final int DEFAULT_HEIGHT = 720;
    public static final float DEFAULT_MOVEMENT_SPEED = 0.10f;
    public static final float UPDATES_PER_SECOND = 20.0f;
    public static final int RENDER_DISTANCE = 2;
    public static Color SKY = new Color(0, 0.59f, 0.229f, 1.0f);

    private boolean debug = false;
    private long window;

    private InputHandler inputHandler;
    private UpdateHandler updateHandler;

    private StaticShader shader;
    private Camera camera;
    private Renderer renderer;

    private World world;
    private PlayerEntity player;

    @Setter private List<CubeModel> renderModels = new CopyOnWriteArrayList<>();
    @Setter private List<CubeModel> nextModels = new CopyOnWriteArrayList<>();

    @Setter private boolean modelNeedsChange = false;
    @Setter private boolean fullscreen;

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
        glfwFocusWindow(window);

        double previousTime = System.nanoTime();
        double lag = 0.0;

        int[] frameCount = {0}; // Track the number of frames using an array for immutability in lambda
        long[] fpsTimer = {System.currentTimeMillis()};

        while (!GLFW.glfwWindowShouldClose(window)) {
            double currentTime = System.nanoTime();
            double elapsedTime = currentTime - previousTime;
            previousTime = currentTime;
            lag += elapsedTime;

            renderer.prepareRender();

            inputHandler.processInput();

            GLFW.glfwPollEvents();

            // Update game logic with a fixed time-step
            double nanosecondsPerUpdate = 1_000_000_000.0 / UPDATES_PER_SECOND;

            while (lag >= nanosecondsPerUpdate) {
                updateHandler.update();
                lag -= nanosecondsPerUpdate;
            }

            shader.start();

            renderer.render(renderModels);

            shader.stop();

            GLFW.glfwSwapBuffers(window);

            updateWindowTitleWithFPS(frameCount, fpsTimer);
        }
    }

    private void updateWindowTitleWithFPS(int[] frameCount, long[] fpsTimer) {
        frameCount[0]++;

        if (System.currentTimeMillis() - fpsTimer[0] >= 1000) {

            String windowTitle = "Game - FPS: " + frameCount[0] +
                    "   Player - x: " + player.getPosition().x + " y: " + player.getPosition().y + " z: " + player.getPosition().z +
                    "      Total Chunks - " + world.getChunkCache().size() +
                    "      Render Models - " + renderModels.size() +
                    "      Trigs - " + (renderer.getVboInstanceData().getCount() / 3);

            glfwSetWindowTitle(window, windowTitle);
            frameCount[0] = 0;
            fpsTimer[0] += 1000;
        }
    }

    private void initClasses() {
        shader = new StaticShader();
        camera = new Camera();
        player = new PlayerEntity(0, (float) Chunk.CHUNK_HEIGHT / 4, 0, camera);
        renderer = new Renderer(shader, camera);
        world = new World();
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
        // Center the cursor and hide it
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

    private void exitGracefully() {
        updateHandler.cleanUp();

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();

        shader.cleanUp();
        renderer.cleanup();
    }

    // Utility methods

    public int[] getCurrentWindowSize() {
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(window, pWidth, pHeight);

            return new int[]{pWidth.get(0), pHeight.get(0)};
        }
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

    public void changeDebug(){
        if (debug){
            debug = false;
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        } else {
            debug = true;
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        }
    }
}

