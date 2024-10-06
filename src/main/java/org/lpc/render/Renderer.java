package org.lpc.render;

import org.joml.Matrix4f;
import org.lpc.Game;
import org.lpc.render.shaders.ShaderLoader;
import org.lpc.render.shaders.TileVertex;
import org.lpc.render.textures.Texture;
import org.lpc.render.textures.TextureHandler;
import org.lpc.world.World;
import org.lpc.world.tiles.AbstractTile;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL11C.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL15C.*;
import static org.lwjgl.opengl.GL15C.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryStack.stackPush;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL15C;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;


public class Renderer {
    private static final int TILE_SIZE = 32;

    private int shaderProgram;
    private final TextureHandler textureHandler;
    private final World world;
    private final Camera camera;

    private final int uniformViewMatrixLocation;
    private final int uniformProjectionMatrixLocation;

    private int vaoId;
    private int vboId;

    private List<TileVertex> vertices;

    public Renderer() {
        Game game = Game.getInstance();
        textureHandler = game.getTextureHandler();
        world = game.getWorld();
        camera = new Camera(0, 0); // Initialize camera at position (0, 0)

        // Initialize vertex data list
        vertices = new ArrayList<>();

        // Load and compile shaders
        shaderProgram = loadShaders();

        // Get the uniform locations for the matrices in the shader
        uniformViewMatrixLocation = glGetUniformLocation(shaderProgram, "viewMatrix");
        uniformProjectionMatrixLocation = glGetUniformLocation(shaderProgram, "projectionMatrix");

        setupBuffers(); // Call method to setup VBO and VAO
    }

    private int loadShaders() {
        try {
            return ShaderLoader.createProgram(
                    "src/main/resources/shaders/vertex.glsl",
                    "src/main/resources/shaders/fragment.glsl"
            );
        } catch (Exception e) {
            e.printStackTrace();
            return 0; // Return an invalid ID on failure
        }
    }

    private void setupBuffers() {
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        vboId = GL15C.glGenBuffers();
        GL15C.glBindBuffer(GL_ARRAY_BUFFER, vboId);

        // Define vertex attribute pointers
        setupVertexAttributes();

        GL15C.glBindBuffer(GL_ARRAY_BUFFER, 0); // Unbind the VBO
        glBindVertexArray(0); // Unbind the VAO
    }

    private void setupVertexAttributes() {
        glVertexAttribPointer(0, 2, GL_FLOAT, false, TileVertex.SIZE * Float.BYTES, 0); // Position
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 2, GL_FLOAT, false, TileVertex.SIZE * Float.BYTES, 2 * Float.BYTES); // TexCoord
        glEnableVertexAttribArray(1);
    }

    public void renderGame() {
        clearScreen();
        glUseProgram(shaderProgram);

        camera.update();
        setupMatrices();

        // Render each tile immediately after binding its texture
        for (int i = 0; i < world.getWidth(); i++) {
            for (int j = 0; j < world.getHeight(); j++) {
                AbstractTile tile = world.getTileAt(i, j);
                if (tile != null) {
                    renderTile(tile);
                }
            }
        }

        glUseProgram(0); // Unbind the shader program
    }

    private void clearScreen() {
        GL11C.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
        GL11C.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    private void setupMatrices() {
        setViewMatrix();
        setProjectionMatrix();
    }

    private void setViewMatrix() {
        try (MemoryStack stack = stackPush()) {
            Matrix4f viewMatrix = new Matrix4f()
                    .identity()
                    .translate((float) -camera.getX(), (float) -camera.getY(), 0); // Translate according to the camera

            glUniformMatrix4fv(uniformViewMatrixLocation, false, viewMatrix.get(stack.mallocFloat(16)));
        }
    }

    private void setProjectionMatrix() {
        try (MemoryStack stack = stackPush()) {
            Game game = Game.getInstance();
            int windowWidth = game.getCurrentWindowSize()[0];
            int windowHeight = game.getCurrentWindowSize()[1];

            Matrix4f projectionMatrix = new Matrix4f()
                    .ortho(0, windowWidth, windowHeight, 0, -1, 1); // Orthographic projection

            glUniformMatrix4fv(uniformProjectionMatrixLocation, false, projectionMatrix.get(stack.mallocFloat(16)));
        }
    }

    private void renderTile(AbstractTile tile) {
        Texture texture = textureHandler.getTexture(tile.getTextureID()); // Get the texture associated with the tile
        if (texture != null) {
            texture.bind(); // Bind the texture

            // Clear previous vertices
            vertices.clear();

            // Add the tile's vertices to the list
            addTileVertices(tile);

            // Upload vertex data to the GPU
            GL15C.glBindBuffer(GL_ARRAY_BUFFER, vboId);
            FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.size() * TileVertex.SIZE);
            for (TileVertex vertex : vertices) {
                vertexBuffer.put(vertex.position);
                vertexBuffer.put(vertex.texCoord);
            }
            vertexBuffer.flip(); // Prepare the buffer for reading
            GL15C.glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_DYNAMIC_DRAW); // Upload data

            // Render the tile
            glBindVertexArray(vaoId);
            GL11C.glDrawArrays(GL_TRIANGLES, 0, vertices.size()); // Draw the vertices
            glBindVertexArray(0);
        }
    }

    private void addTileVertices(AbstractTile tile) {
        float x = tile.getX() * TILE_SIZE;
        float y = tile.getY() * TILE_SIZE;

        // Define 2 triangles (6 vertices total) for the quad
        vertices.add(new TileVertex(x, y, 0, 0)); // Bottom left
        vertices.add(new TileVertex(x + TILE_SIZE, y, 1, 0)); // Bottom right
        vertices.add(new TileVertex(x + TILE_SIZE, y + TILE_SIZE, 1, 1)); // Top right

        vertices.add(new TileVertex(x, y, 0, 0)); // Bottom left
        vertices.add(new TileVertex(x + TILE_SIZE, y + TILE_SIZE, 1, 1)); // Top right
        vertices.add(new TileVertex(x, y + TILE_SIZE, 0, 1)); // Top left
    }
}