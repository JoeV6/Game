package org.lpc.render.pipeline;

import org.lpc.render.pipeline.models.RawModel;
import org.lpc.render.pipeline.textures.Texture;
import org.lpc.render.pipeline.textures.TextureLoader;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15C;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL30C.glBindVertexArray;
import static org.lwjgl.opengl.GL30C.glGenVertexArrays;

public class ModelLoader {
    private final List<Integer> vaos = new ArrayList<>();
    private final List<Integer> vbos = new ArrayList<>();
    private final List<Integer> textures = new ArrayList<>();

    public RawModel loadToVAO(float[] positions, int[] indices, float[] textureCoords) {
        int vaoID = createVAO();
        storeDataInAttributeList(0, 3, positions);
        storeDataInAttributeList(1, 2, textureCoords);
        bindIndicesBuffer(indices);
        unbindVAO();
        return new RawModel(vaoID, indices.length);
    }

    public int loadTexture(String fileName){
        Texture texture = TextureLoader.getTexture("src/main/resources/textures/" + fileName + ".png");
        textures.add(texture.getTextureID());
        return texture.getTextureID();
    }

    private int createVAO() {
        int vaoID = glGenVertexArrays();
        vaos.add(vaoID);
        glBindVertexArray(vaoID);

        return vaoID;
    }

    private void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data) {
        int vboID = GL30.glGenBuffers();
        vbos.add(vboID);
        GL15C.glBindBuffer(GL15C.GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = storeDataInFloatBuffer(data);
        GL15C.glBufferData(GL15C.GL_ARRAY_BUFFER, buffer, GL15C.GL_STATIC_DRAW);
        GL30.glVertexAttribPointer(attributeNumber, coordinateSize, GL15C.GL_FLOAT, false, 0, 0);
        GL15C.glBindBuffer(GL15C.GL_ARRAY_BUFFER, 0);
    }

    private void unbindVAO() {
        GL30.glBindVertexArray(0);
    }

    private void bindIndicesBuffer(int[] indices) {
        int vboID = GL15C.glGenBuffers();
        vbos.add(vboID);
        GL15C.glBindBuffer(GL15C.GL_ELEMENT_ARRAY_BUFFER, vboID);
        IntBuffer buffer = storeDataInIntBuffer(indices);
        GL15C.glBufferData(GL15C.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15C.GL_STATIC_DRAW);
        GL15C.glBufferData(GL15C.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15C.GL_STATIC_DRAW);
    }

    private IntBuffer storeDataInIntBuffer(int[] data) {
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    private FloatBuffer storeDataInFloatBuffer(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    public void cleanUp() {
        for (int vao : vaos) {
            GL30.glDeleteVertexArrays(vao);
        }
        for (int vbo : vbos) {
            GL15C.glDeleteBuffers(vbo);
        }
        for (int texture : textures) {
            GL15C.glDeleteBuffers(texture);
        }
    }
}
