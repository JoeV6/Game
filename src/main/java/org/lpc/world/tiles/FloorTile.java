package org.lpc.world.tiles;

import lombok.Getter;

@Getter
public abstract class FloorTile extends AbstractTile {
    private final double walkSpeedModifier;

    public FloorTile(int x, int y, double width, double height, int textureID, int tileID, double walkSpeedModifier) {
        super(x, y, width, height, textureID, tileID, false);
        this.walkSpeedModifier = walkSpeedModifier;
    }
}
