package org.lpc.render;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;
import org.lpc.world.World;
import org.lpc.world.block.AbstractBlock;
import org.lpc.world.chunk.Chunk;
import org.lpc.world.entity.entities.PlayerEntity;

@Getter @Setter
public class Camera {
    private Vector3f position;
    private float pitch;
    private float yaw;
    private float roll;

    public Camera() {
        this.position = new Vector3f(0,0,0);
        this.pitch = 0;
        this.yaw = 0;
        this.roll = 0;
    }

    public void move(float dx, float dy, float dz) {
        position.add(dx, dy, dz);
    }

    public void rotate(float dyaw, float dpitch, float droll) {
        yaw += dyaw;
        pitch += dpitch;
        roll += droll;
    }
}
