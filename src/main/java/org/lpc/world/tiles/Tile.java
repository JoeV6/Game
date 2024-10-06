package org.lpc.world.tiles;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Tile {
    private int x, y;
    private int width, height;
    private int textureID;
    protected boolean isSolid;

    public Tile(int x, int y, int width, int height, int textureID, boolean isSolid) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.textureID = textureID;
        this.isSolid = isSolid;
    }

    public void render() {
        // Render the tile
    }
}
