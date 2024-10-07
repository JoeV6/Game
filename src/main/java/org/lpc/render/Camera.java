package org.lpc.render;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;

@Getter @Setter
public class Camera {
    private Vector3f position = new Vector3f(0, 0, 0);
    private float pitch = 0;
    private float yaw = 0;
    private float roll = 0;

    public Camera() {
    }

    public void move(float dx, float dy, float dz) {
        position.add(dx, dy, dz);
    }

    public void rotate(float dpitch, float dyaw, float droll) {
        pitch += dpitch;
        yaw += dyaw;
        roll += droll;
    }
}
