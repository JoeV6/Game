package org.lpc.world.tiles;

import lombok.Getter;
import lombok.Setter;
import org.lpc.Game;
import org.lpc.render.Renderer;
import org.lpc.render.textures.TextureHandler;

@Getter
@Setter
public abstract class AbstractTile {
    protected int x, y;
    protected int width, height;
    protected int textureID;
    protected int tileID;
    protected boolean isSolid;
    protected Game game;
    protected TextureHandler textureHandler;
    protected String textureLocation;


    public AbstractTile(int x, int y, int width, int height, int textureID, int tileID, boolean isSolid) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.textureID = textureID;
        this.tileID = tileID;
        this.isSolid = isSolid;
        this.game = Game.getInstance();
        this.textureHandler = game.getTextureHandler();
        this.textureLocation = "src/main/resources/textures/tile_" + tileID + ".png";
    }
}