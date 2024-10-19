package org.lpc.world.entity.entities;

import org.joml.Vector3f;
import org.lpc.Game;
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
    }

    public void update(){
        System.out.println(getBlockLookingAt());

        this.move(vx, vy, vz);
    }

    private void applyGravity(){

    }

    private boolean checkCollision(){


        return false;
    }

    public AbstractBlock getBlockLookingAt() {
        int maxDistance = 5; // Maximum distance to check
        float stepSize = 0.5f; // Step size for ray marching (smaller = more accurate)

        Vector3f cameraPos = new Vector3f(camera.getPosition());

        Vector3f direction = getDirectionFromCamera(camera.getYaw(), camera.getPitch());

        for (float t = 0; t < maxDistance; t += stepSize) {
            Vector3f currentPos = new Vector3f(cameraPos).add(direction.x * t, -direction.y * t, direction.z * t);

            int blockX = (int) Math.floor(currentPos.x);
            int blockY = (int) Math.floor(currentPos.y);
            int blockZ = (int) Math.floor(currentPos.z);

            AbstractBlock block = game.getWorld().getBlockAt(blockX, blockY, blockZ);

            if (block != null) {
                return block;
            }
        }

        return null;
    }

    private Vector3f getDirectionFromCamera(float yaw, float pitch) {
        float pitchRadians = (float) Math.toRadians(pitch);
        float yawRadians = (float) Math.toRadians(yaw);

        float x = (float) (Math.cos(pitchRadians) * Math.sin(yawRadians));
        float y = (float) Math.sin(pitchRadians);
        float z = (float) (Math.cos(pitchRadians) * Math.cos(yawRadians));

        return new Vector3f(x, y, z).normalize();
    }


    // Movement methods

    public void move(float dx, float dy, float dz){
        x += dx;
        y += dy;
        z += dz;

        camera.move(dx, dy, dz);
    }

    public void setPos(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;

        camera.getPosition().x = x;
        camera.getPosition().y = y;
        camera.getPosition().z = z;
    }

    public void moveForward(float amount){
        x += (float) Math.sin(Math.toRadians(camera.getYaw())) * amount;
        y += 0;
        z += (float) -Math.cos(Math.toRadians(camera.getYaw())) * amount;


        camera.moveForward(amount);
    }

    public void moveLeft(float amount){
        x += (float) Math.sin(Math.toRadians(camera.getYaw() - 90)) * amount;
        y += 0;
        z += (float) -Math.cos(Math.toRadians(camera.getYaw() - 90)) * amount;

        camera.moveLeft(amount);
    }

    public void rotate(float dyaw, float dpitch, float droll){
        camera.rotate(dyaw, dpitch, droll);
    }

    public Vector3f getPosition(){
        return new Vector3f(x, y, z);
    }
}
