package org.lpc.world.entity;

import org.lpc.Game;

public abstract class Entity {
    protected double x, y;
    protected double width, height;
    protected int textureID;
    protected int entityID;
    protected Game game;
    //protected TextureHandler textureHandler;
    protected String textureLocation;

    public Entity(int x, int y, int width, int height, int textureID, int entityID) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.textureID = textureID;
        this.entityID = entityID;
        this.game = Game.getInstance();
        //this.textureHandler = game.getTextureHandler();
        this.textureLocation = "src/main/resources/textures/entity_" + entityID + ".png";
    }
}
