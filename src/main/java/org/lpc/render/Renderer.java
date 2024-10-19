package org.lpc.render;

import lombok.Getter;
import org.joml.Matrix4f;
import org.lpc.Game;
import org.lpc.render.pipeline.models.FullModel;
import org.lpc.render.pipeline.models.RawModel;

import static org.lwjgl.opengl.GL11C.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL20.*;

import org.lpc.render.pipeline.shaders.StaticShader;
import org.lpc.utils.Maths;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL13C;
import org.lwjgl.opengl.GL20C;
import org.lwjgl.opengl.GL30C;

@Getter
public class Renderer {

    private static final float FOV = 110;
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 1000;

    private final Matrix4f projectionMatrix;
    private final Camera camera;

    public Renderer(StaticShader shader, Camera camera) {
        Game game = Game.getInstance();
        this.camera = camera;

        projectionMatrix = Maths.createProjectionMatrix(
                game.getCurrentWindowSize()[0],
                game.getCurrentWindowSize()[1],
                FOV, NEAR_PLANE, FAR_PLANE);

        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }

    public void prepare() {
        glEnable(GL11C.GL_DEPTH_TEST);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glClearColor(0f, 0.827f, 1f, 1.0f);
        //glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
    }

    public void render(FullModel fullModel, StaticShader shader) {
        RawModel rawModel = fullModel.getModel().getRawModel();

        GL30C.glBindVertexArray(rawModel.getVaoID());
        GL20C.glEnableVertexAttribArray(0);
        GL20C.glEnableVertexAttribArray(1);

        shader.loadViewMatrix(camera);

        Matrix4f transformMatrix = Maths.createTransformationMatrix(
                fullModel.getPosition(), fullModel.getRotX(), fullModel.getRotY(), fullModel.getRotZ(), fullModel.getScale());

        shader.loadTransformMatrix(transformMatrix);

        GL13C.glActiveTexture(GL13C.GL_TEXTURE0);

        if(fullModel.getModel().getTexture().getTextureID() != -1)
            GL13C.glBindTexture(GL11C.GL_TEXTURE_2D, fullModel.getModel().getTexture().getTextureID());

        GL11C.glDrawElements(GL_TRIANGLES, rawModel.getVertexCount(), GL_UNSIGNED_INT, 0);
        GL20C.glDisableVertexAttribArray(0);
        GL20C.glDisableVertexAttribArray(1);
        GL30C.glBindVertexArray(0);
    }
}