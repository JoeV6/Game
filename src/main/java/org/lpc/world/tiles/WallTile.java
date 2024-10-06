package org.lpc.world.tiles;

public class WallTile extends AbstractTile {
    public WallTile(int x, int y, int width, int height, int textureID, int tileID) {
        super(x, y, width, height, textureID, tileID, true);
    }
}
