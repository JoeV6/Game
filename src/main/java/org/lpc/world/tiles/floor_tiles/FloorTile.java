package org.lpc.world.tiles.floor_tiles;

import lombok.Getter;
import org.lpc.world.tiles.Tile;

@Getter
public class FloorTile extends Tile {
    private final double walkSpeedModifier;

    public FloorTile(int x, int y, int width, int height, int textureID, double walkSpeedModifier) {
        super(x, y, width, height, textureID, false);
        this.walkSpeedModifier = walkSpeedModifier;
    }
}
