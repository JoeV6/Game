package org.lpc.world.entity.entities;

import org.joml.Vector3f;
import org.lpc.render.Camera;
import org.lpc.world.block.AbstractBlock;
import org.lpc.world.entity.Entity;

public class PlayerEntity extends Entity {
    public static float PLAYER_HEIGHT = 1;
    public static float PLAYER_WIDTH = 0.8f;

    private final Camera camera;

    public PlayerEntity(float x, float y, float z, Camera camera) {
        super(x, y, z, PLAYER_WIDTH, PLAYER_HEIGHT, 0);
        this.camera = camera;

        setPos(x, y, z);
        applyGravity();
    }

    public void update(){
        this.move(vx, vy, vz);
    }

    private void applyGravity(){

    }

    private boolean checkCollision(){
        return false;
    }

    public AbstractBlock getFirstBlockInFront(float maxDistance, float stepSize) {
        Vector3f position = camera.getPosition();
        float yaw = camera.getYaw();
        float pitch = camera.getPitch();

        float yawRad = (float) Math.toRadians(yaw);
        float pitchRad = (float) Math.toRadians(pitch);

        float dirX = (float) Math.sin(yawRad) * (float) Math.cos(pitchRad);
        float dirY = (float) -Math.sin(pitchRad);
        float dirZ = (float) -Math.cos(yawRad) * (float) Math.cos(pitchRad);

        float currentX = position.x;
        float currentY = position.y;
        float currentZ = position.z;

        for (float distance = 0; distance < maxDistance; distance += stepSize) {
            float checkX = currentX + dirX * distance;
            float checkY = currentY + dirY * distance;
            float checkZ = currentZ + dirZ * distance;

            int blockX = (int) Math.floor(checkX);
            int blockY = (int) Math.floor(checkY);
            int blockZ = (int) Math.floor(checkZ);

            AbstractBlock block = game.getWorld().getBlockAt(blockX, blockY, blockZ);

            if (block != null && block.getBlockID() != -1) {
                return block;
            }
        }
        return null;
    }


    // Movement methods

    public void move(float dx, float dy, float dz){
        if(dx >= 50 || dy >= 50 || dz >= 50 ){
            System.out.println("PlayerEntity.move: dx=" + dx + ", dy=" + dy + ", dz=" + dz);
            return;
        }

        x += dx;
        y += dy;
        z += dz;

        camera.move(dx, dy, dz);
    }

    public void setPos(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;

        camera.setPosition(new Vector3f(x, y, z));
    }

    public void moveForward(float amount){
        float pitchRad = (float) Math.toRadians(camera.getPitch());
        float yawRad = (float) Math.toRadians(camera.getYaw());

        float dirX = (float) Math.sin(yawRad) * (float) Math.cos(pitchRad);
        float dirY = (float) -Math.sin(pitchRad);
        float dirZ = (float) -Math.cos(yawRad) * (float) Math.cos(pitchRad);

        move(dirX * amount, dirY * amount, dirZ * amount);
    }

    public void moveLeft(float amount){
        float dirX = (float) Math.sin(Math.toRadians(camera.getYaw() - 90));
        float dirY = 0;
        float dirZ = (float) -Math.cos(Math.toRadians(camera.getYaw() - 90));

        move(dirX * amount, dirY, dirZ * amount);
    }

    public Vector3f getPosition(){
        return new Vector3f(x, y, z);
    }
}
