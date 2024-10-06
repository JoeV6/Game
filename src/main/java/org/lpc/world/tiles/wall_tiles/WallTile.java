package org.lpc.world.tiles.wall_tiles;

import org.lpc.world.tiles.Tile;

public class WallTile extends Tile {

    public WallTile(int x, int y, int width, int height, int textureID) {
        super(x, y, width, height, textureID, true);
        this.isSolid = true;
    }
}
