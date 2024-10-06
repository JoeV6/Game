package org.lpc.render.shaders;

public class TileVertex {
    public static final int SIZE = 4;

    public final float[] position;  // x, y
    public final float[] texCoord;   // u, v

    public TileVertex(float x, float y, float u, float v) {
        this.position = new float[]{x, y};
        this.texCoord = new float[]{u, v};
    }
}

