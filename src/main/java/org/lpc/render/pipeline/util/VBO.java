package org.lpc.render.pipeline.util;

import lombok.Getter;

import static org.lwjgl.opengl.GL15.*;

@Getter
public class VBO {
    private final int id;
    private int count;

    public VBO() {
        id = glGenBuffers();
    }

    public void bind(int target) {
        glBindBuffer(target, id);
    }

    public void uploadData(float[] data, int usage) {
        bind(GL_ARRAY_BUFFER);
        glBufferData(GL_ARRAY_BUFFER, data, usage);
        count = data.length;
    }

    public void uploadData(int[] data, int usage) {
        bind(GL_ELEMENT_ARRAY_BUFFER);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, data, usage);
        count = data.length;
    }

    public void unbind(int target) {
        glBindBuffer(target, 0);
    }

    public void delete() {
        glDeleteBuffers(id);
    }
}

