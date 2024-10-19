package org.lpc.render.pipeline.util;

import lombok.Getter;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL33C.glVertexAttribDivisor;

@Getter
public class VAO {
    private final int id;

    public VAO() {
        id = glGenVertexArrays();
    }

    public void bind() {
        glBindVertexArray(id);
    }

    public void unbind() {
        glBindVertexArray(0);
    }

    public void linkAttrib(VBO vbo, int layout, int size, int type, int stride, int offset) {
        vbo.bind(GL_ARRAY_BUFFER);
        glVertexAttribPointer(layout, size, type, false, stride, offset);
        glEnableVertexAttribArray(layout);
        vbo.unbind(GL_ARRAY_BUFFER);
    }

    public void linkAttribInstanced(VBO vbo, int layout, int size, int type, int stride, int offset, int divisor) {
        vbo.bind(GL_ARRAY_BUFFER);
        glVertexAttribPointer(layout, size, type, false, stride, offset);
        glEnableVertexAttribArray(layout);
        glVertexAttribDivisor(layout, divisor); // divisor of 1 means it's per-instance data
        vbo.unbind(GL_ARRAY_BUFFER);
    }

    public void bindEBO(VBO ebo) {
        ebo.bind(GL_ELEMENT_ARRAY_BUFFER);
    }

    public void delete() {
        glDeleteVertexArrays(id);
    }

}
