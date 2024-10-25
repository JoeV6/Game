package org.lpc.render.pipeline.models;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

@Getter @Setter
public class CubeModel implements Serializable {
    public static final float[] vertices = { -0.5f,0.5f,-0.5f, -0.5f,-0.5f,-0.5f, 0.5f,-0.5f,-0.5f, 0.5f,0.5f,-0.5f, -0.5f,0.5f,0.5f, -0.5f,-0.5f,0.5f, 0.5f,-0.5f,0.5f, 0.5f,0.5f,0.5f, 0.5f,0.5f,-0.5f, 0.5f,-0.5f,-0.5f, 0.5f,-0.5f,0.5f, 0.5f,0.5f,0.5f, -0.5f,0.5f,-0.5f, -0.5f,-0.5f,-0.5f, -0.5f,-0.5f,0.5f, -0.5f,0.5f,0.5f, -0.5f,0.5f,0.5f, -0.5f,0.5f,-0.5f, 0.5f,0.5f,-0.5f, 0.5f,0.5f,0.5f, -0.5f,-0.5f,0.5f, -0.5f,-0.5f,-0.5f, 0.5f,-0.5f,-0.5f, 0.5f,-0.5f,0.5f };
    public static final float[] textureCoords = { 0,0, 0,1, 1,1, 1,0, 0,0, 0,1, 1,1, 1,0, 0,0, 0,1, 1,1, 1,0, 0,0, 0,1, 1,1, 1,0, 0,0, 0,1, 1,1, 1,0, 0,0, 0,1, 1,1, 1,0};
    public static final int[] indices = {0,1,3, 3,1,2, 4,5,7, 7,5,6, 8,9,11, 11,9,10, 12,13,15, 15,13,14, 16,17,19, 19,17,18, 20,21,23, 23,21,22};

    private Vector3f position;
    private float rotationX, rotationY, rotationZ;
    private float scale;
    private int textureID;

    public CubeModel(Vector3f position, int textureID) {
        this.position = position;
        this.rotationX = 0;
        this.rotationY = 0;
        this.rotationZ = 0;
        this.scale = 1;
        this.textureID = textureID;
    }


    // Custom serialization
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

        out.writeFloat(position.x);
        out.writeFloat(position.y);
        out.writeFloat(position.z);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        float x = in.readFloat();
        float y = in.readFloat();
        float z = in.readFloat();
        position = new Vector3f(x, y, z);
    }
}
