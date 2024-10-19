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

    public void moveForward(float amount){
        //position.add((float) Math.sin(Math.toRadians(yaw)) * amount, 0, (float) -Math.cos(Math.toRadians(yaw)) * amount);
        float pitchRad = (float) Math.toRadians(pitch);
        float yawRad = (float) Math.toRadians(yaw);

        float dirX = (float) Math.sin(yawRad) * (float) Math.cos(pitchRad);
        float dirY = (float) -Math.sin(pitchRad);
        float dirZ = (float) -Math.cos(yawRad) * (float) Math.cos(pitchRad);

        position.add(
                dirX * amount,
                dirY * amount,
                dirZ * amount
        );
    }

    public void moveLeft(float amount){
        position.add((float) Math.sin(Math.toRadians(yaw - 90)) * amount, 0, (float) -Math.cos(Math.toRadians(yaw - 90)) * amount);
    }

    public void rotate(float dyaw, float dpitch, float droll) {
        yaw += dyaw;
        pitch += dpitch;
        roll += droll;
    }
}
