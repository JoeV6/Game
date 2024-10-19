package org.lpc.render.pipeline.shaders;

import org.joml.Matrix4f;
import org.lpc.render.Camera;
import org.lpc.utils.Maths;

public class StaticShader extends ShaderProgram{

    private static final String VERTEX = "src/main/resources/shaders/vertex.glsl";
    private static final String FRAGMENT = "src/main/resources/shaders/fragment.glsl";

    private int location_projectionMatrix;
    private int location_viewMatrix;

    public StaticShader() {
        super(VERTEX, FRAGMENT);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
    }

    @Override
    protected void getAllUniformLocations() {
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
    }

    public void loadProjectionMatrix(Matrix4f matrix) {
        super.loadMatrix(location_projectionMatrix, matrix);
    }

    public void loadViewMatrix(Camera camera){
        Matrix4f viewMatrix = Maths.createViewMatrix(camera);
        super.loadMatrix(location_viewMatrix, viewMatrix);
    }
}
