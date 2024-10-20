package org.lpc.render.pipeline;

import lombok.Getter;
import org.joml.Vector3f;
import org.lpc.Game;
import org.lpc.render.Camera;
import org.lpc.render.pipeline.shaders.StaticShader;
import org.lpc.render.pipeline.textures.Texture;
import org.lpc.render.pipeline.textures.TextureLoader;
import org.lpc.render.pipeline.models.CubeModel;
import org.lpc.render.pipeline.util.VAO;
import org.lpc.render.pipeline.util.VBO;
import org.lpc.utils.Maths;
import org.lpc.utils.TextureAtlas;
import org.lwjgl.opengl.GL13C;

import java.util.List;

import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20C.glGetUniformLocation;
import static org.lwjgl.opengl.GL30C.GL_TEXTURE_2D_ARRAY;
import static org.lwjgl.opengl.GL31C.glDrawElementsInstanced;

@Getter
public class Renderer {
    private static final float FOV = 110;
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 1000;

    private VAO vao;
    private VBO vboVertices;
    private VBO vboIndices;
    private VBO vboTexCoords;
    private VBO vboInstanceData;
    private VBO vboTexIds;

    private final Camera camera;
    private final StaticShader shader;

    private final Texture texture;
    private final TextureAtlas textureAtlas;

    public Renderer(StaticShader shader, Camera camera) {
        textureAtlas = TextureAtlas.getInstance();
        textureAtlas.createTextureAtlas("src/main/resources/textures/blocks");
        texture = TextureLoader.getTexture(TextureAtlas.ATLAS_PATH);

        this.camera = camera;
        this.shader = shader;

        prepareVAO();
        prepareShader();

        initLineRender();
    }

    private void prepareShader() {
        Game game = Game.getInstance();
        shader.start();
        shader.loadProjectionMatrix(
                Maths.createProjectionMatrix(
                        game.getCurrentWindowSize()[0],
                        game.getCurrentWindowSize()[1],
                        FOV, NEAR_PLANE, FAR_PLANE
                )
        );
        shader.stop();
    }

    private void prepareVAO() {
        vao = new VAO();
        vao.bind();

        // VBO for vertices
        vboVertices = new VBO();
        vboVertices.uploadData(CubeModel.vertices, GL_STATIC_DRAW);
        vao.linkAttrib(vboVertices, 0, 3, GL_FLOAT, 0, 0); // Vertex positions

        // VBO for indices
        vboIndices = new VBO();
        vboIndices.uploadData(CubeModel.indices, GL_STATIC_DRAW);

        // VBO for offset data (instance data)
        vboInstanceData = new VBO();
        vboInstanceData.uploadData(new float[0], GL_DYNAMIC_DRAW);
        vao.linkAttribInstanced(vboInstanceData, 2, 3, GL_FLOAT, 3 * Float.BYTES, 0, 1);

        // VBO for texture coords (Shared for all instances)
        vboTexCoords = new VBO();
        vboTexCoords.uploadData(textureAtlas.getAllTextureCoords(), GL_STATIC_DRAW); // Upload once
        vao.linkAttrib(vboTexCoords, 1, 2, GL_FLOAT, 0, 0); // Each vertex has 2 floats

        // VBO for texture IDs (instance data)
        vboTexIds = new VBO();
        vboTexIds.uploadData(new float[0], GL_DYNAMIC_DRAW);
        vao.linkAttribInstanced(vboTexIds, 3, 1, GL_FLOAT, 0, 0, 1);

        vao.unbind();
    }

    public void prepareRender() {
        glEnable(GL_DEPTH_TEST);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glClearColor(0.6f, 0.8f, 1, 1);
        glDepthFunc(GL_LESS);      // Specify the depth comparison function (GL_LESS is common)

        //glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        glCullFace(GL_BACK);
    }

    public void render(List<CubeModel> cubes) {
        vboInstanceData.uploadData(generateInstanceData(cubes), GL_DYNAMIC_DRAW);
        vboTexIds.uploadData(generateTexcoordData(cubes), GL_DYNAMIC_DRAW);

        shader.start();
            shader.loadViewMatrix(camera);
            shader.loadTextureArray(0);

            GL13C.glActiveTexture(GL13C.GL_TEXTURE0);
            GL13C.glBindTexture(GL_TEXTURE_2D_ARRAY, texture.getTextureID());

            vao.bind();
                glDrawElementsInstanced(GL_TRIANGLES, vboIndices.getCount(), GL_UNSIGNED_INT, 0, cubes.size());
            vao.unbind();
        shader.stop();

        renderCrosshair();

        glfwSwapBuffers(Game.getInstance().getWindow());
    }

    private float[] generateInstanceData(List<CubeModel> cubes) {
        int instanceCount = cubes.size();
        float[] instanceData = new float[instanceCount * 3]; // 3 floats per offset (x, y, z)

        for (int i = 0; i < instanceCount; i++) {
            CubeModel cube = cubes.get(i);
            Vector3f position = cube.getPosition();

            instanceData[i * 3] = position.x;
            instanceData[i * 3 + 1] = position.y;
            instanceData[i * 3 + 2] = position.z;
        }

        return instanceData;
    }

    private float[] generateTexcoordData(List<CubeModel> cubes) {
        int instanceCount = cubes.size();
        float[] texcoordData = new float[instanceCount]; // 1 float for each instance reference to texture

        for (int i = 0; i < instanceCount; i++) {
            CubeModel cube = cubes.get(i);
            texcoordData[i] = cube.getTextureID(); // Store the texture ID or index
        }

        return texcoordData;
    }

    public void cleanup() {
        vboVertices.delete();
        vboTexCoords.delete();
        vboIndices.delete();
        vboInstanceData.delete();
        vao.delete();
        lineVAO.delete();
        lineVBO.delete();
    }

    private VAO lineVAO;
    private VBO lineVBO;

    private void initLineRender() {
        lineVAO = new VAO();
        lineVAO.bind();

        float[] lineVertices = {
                -0.02f, 0.0f,
                0.02f, 0.0f,
                0.0f, -0.02f,
                0.0f, 0.02f
        };

        lineVBO = new VBO();
        lineVBO.uploadData(lineVertices, GL_STATIC_DRAW);
        lineVAO.linkAttrib(lineVBO, 0, 2, GL_FLOAT, 0, 0); // 2D positions

        lineVAO.unbind();
    }

    private void renderCrosshair() {
        lineVAO.bind();

        glLineWidth(2.0f); // Set line width
        glDrawArrays(GL_LINES, 0, 4); // Draw the two lines (4 vertices)

        lineVAO.unbind();
    }
}