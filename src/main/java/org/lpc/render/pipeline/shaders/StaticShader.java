package org.lpc.render.pipeline.shaders;

public class StaticShader extends ShaderProgram{

    private static final String VERTEX = "src/main/resources/shaders/vertex.glsl";
    private static final String FRAGMENT = "src/main/resources/shaders/fragment.glsl";

    public StaticShader() {
        super(VERTEX, FRAGMENT);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
    }
}
