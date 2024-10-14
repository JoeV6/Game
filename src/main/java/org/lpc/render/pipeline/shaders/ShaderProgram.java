package org.lpc.render.pipeline.shaders;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20C;

import java.io.*;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20C.*;

public abstract class ShaderProgram {
    private final int programID;
    private final int vertexShaderID;
    private final int fragmentShaderID;

    private static final FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

    public ShaderProgram(String vertexFile, String fragmentFile) {
        vertexShaderID = loadShader(vertexFile, GL_VERTEX_SHADER);
        fragmentShaderID = loadShader(fragmentFile, GL_FRAGMENT_SHADER);
        programID = glCreateProgram();
        GL20C.glAttachShader(programID, vertexShaderID);
        GL20C.glAttachShader(programID, fragmentShaderID);
        bindAttributes();
        GL20C.glLinkProgram(programID);
        GL20C.glValidateProgram(programID);
        getAllUniformLocations();
    }

    protected int getUniformLocation(String uniformName) {
        return GL20C.glGetUniformLocation(programID, uniformName);
    }

    protected abstract void getAllUniformLocations();

    protected void loadFloat(int location, float value) {
        GL20C.glUniform1f(location, value);
    }

    protected void loadVector(int location, Vector3f vec) {
        GL20C.glUniform3f(location, vec.x, vec.y, vec.z);
    }

    protected void loadBool(int location, boolean value){
        float toLoad = 0;
        if (value) {
            toLoad = 1;
        }
        GL20C.glUniform1f(location, toLoad);
    }

    protected void loadMatrix(int location, Matrix4f mat){
        matrixToBuffer(mat, matrixBuffer);
        GL20C.glUniformMatrix4fv(location, false, matrixBuffer);
    };

    public void start() {
        GL20C.glUseProgram(programID);
    }

    public void stop() {
        GL20C.glUseProgram(0);
    }

    public void cleanUp() {
        stop();
        GL20C.glDetachShader(programID, vertexShaderID);
        GL20C.glDetachShader(programID, fragmentShaderID);
        GL20C.glDeleteShader(vertexShaderID);
        GL20C.glDeleteShader(fragmentShaderID);
        GL20C.glDeleteProgram(programID);
    }

    protected void bindAttribute(int attribute, String variableName) {
        GL20C.glBindAttribLocation(programID, attribute, variableName);
    }

    protected abstract void bindAttributes();

    private static int loadShader(String file, int type) {
        StringBuilder shaderSource = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                shaderSource.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("Could not read file!");
            e.printStackTrace();
            System.exit(-1);
        }
        int shaderID = glCreateShader(type);
        glShaderSource(shaderID, shaderSource);
        glCompileShader(shaderID);
        if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println("Could not compile shader.");
            System.err.println(glGetShaderInfoLog(shaderID));
            System.exit(-1);
        }
        return shaderID;
    }

    private static void matrixToBuffer(Matrix4f m, FloatBuffer dest)
    {
        matrixToBuffer(m, 0, dest);
    }
    private static void matrixToBuffer(Matrix4f m, int offset, FloatBuffer dest)
    {
        dest.put(offset, m.m00());
        dest.put(offset + 1, m.m01());
        dest.put(offset + 2, m.m02());
        dest.put(offset + 3, m.m03());
        dest.put(offset + 4, m.m10());
        dest.put(offset + 5, m.m11());
        dest.put(offset + 6, m.m12());
        dest.put(offset + 7, m.m13());
        dest.put(offset + 8, m.m20());
        dest.put(offset + 9, m.m21());
        dest.put(offset + 10, m.m22());
        dest.put(offset + 11, m.m23());
        dest.put(offset + 12, m.m30());
        dest.put(offset + 13, m.m31());
        dest.put(offset + 14, m.m32());
        dest.put(offset + 15, m.m33());
    }
}
