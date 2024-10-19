package org.lpc.world.entity;

import lombok.Getter;
import org.lpc.Game;

@Getter
public abstract class Entity {
    protected float x, y, z;
    protected float vx, vy, vz;
    protected float width, height;
    protected int textureID;
    protected int entityID;
    protected Game game;
    protected String textureLocation;

    public Entity(float x, float y, float z, float width, float height, int entityID) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.vx = this.vy = this.vz = 0;
        this.width = width;
        this.height = height;
        this.entityID = entityID;
        this.game = Game.getInstance();
        this.textureLocation = "entities/entity_" + entityID;
    }
}
