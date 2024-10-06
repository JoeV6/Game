package org.lpc.world.tiles;

public abstract class WallTile extends AbstractTile {
    public WallTile(int x, int y, double width, double height, int textureID, int tileID) {
        super(x, y, width, height, textureID, tileID, true);
    }
}
