package org.lpc.world.entity;

import org.lpc.Game;

public abstract class Entity {
    protected double x, y;
    protected double width, height;
    protected int textureID;
    protected int entityID;
    protected Game game;
    protected String textureLocation;

    public Entity(int x, int y, int width, int height, int entityID) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.entityID = entityID;
        this.game = Game.getInstance();
        this.textureLocation = "entities/entity_" + entityID;
    }
}
