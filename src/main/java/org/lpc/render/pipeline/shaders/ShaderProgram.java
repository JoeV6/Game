package org.lpc.render.pipeline.shaders;

import org.lpc.render.pipeline.textures.Texture;
import org.lpc.render.pipeline.textures.TextureLoader;
import org.lwjgl.opengl.GL20C;

import java.io.*;

import static org.lwjgl.opengl.GL20C.*;

public abstract class ShaderProgram {
    private int programID;
    private int vertexShaderID;
    private int fragmentShaderID;

    public ShaderProgram(String vertexFile, String fragmentFile) {
        vertexShaderID = loadShader(vertexFile, GL_VERTEX_SHADER);
        fragmentShaderID = loadShader(fragmentFile, GL_FRAGMENT_SHADER);
        programID = glCreateProgram();
        GL20C.glAttachShader(programID, vertexShaderID);
        GL20C.glAttachShader(programID, fragmentShaderID);
        GL20C.glLinkProgram(programID);
        GL20C.glValidateProgram(programID);
        bindAttributes();
    }

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
}
