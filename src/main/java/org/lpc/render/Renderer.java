package org.lpc.render;

import org.lpc.render.pipeline.models.RawModel;

import static org.lwjgl.opengl.GL11C.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL20.*;

import org.lpc.render.pipeline.models.TexturedModel;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL13C;
import org.lwjgl.opengl.GL20C;
import org.lwjgl.opengl.GL30C;


public class Renderer {
    public void prepare() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
    }

    public void render(TexturedModel model){
        RawModel rawModel = model.getRawModel();
        GL30C.glBindVertexArray(rawModel.getVaoID());
        GL20C.glEnableVertexAttribArray(0);
        GL20C.glEnableVertexAttribArray(1);
        GL13C.glActiveTexture(GL13C.GL_TEXTURE0);
        GL11C.glBindTexture(GL11C.GL_TEXTURE_2D, model.getTexture().getTextureID());
        GL11C.glDrawElements(GL_TRIANGLES, rawModel.getVertexCount(), GL_UNSIGNED_INT, 0);
        GL20C.glDisableVertexAttribArray(0);
        GL20C.glDisableVertexAttribArray(1);
        GL30C.glBindVertexArray(0);
    }
}