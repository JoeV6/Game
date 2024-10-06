package org.lpc.world;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.lpc.world.tiles.AbstractTile;
import org.lpc.world.tiles.FloorTile;
import org.lpc.world.tiles.WallTile;
import org.lpc.world.tiles.floor_tiles.GrassTile;
import org.lpc.world.tiles.wall_tiles.StoneWallTile;

@Getter @Setter
@ToString(exclude = {"tiles"})
public class World {
    private AbstractTile[][] tiles;
    private int width, height;

    public World(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new AbstractTile[width][height];

        initWorld();
    }

    private void initWorld() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (x == 0 || y == 0 || x == width - 1 || y == height - 1) {
                    setTile(x, y, new StoneWallTile(x, y));
                } else {
                    setTile(x, y, new GrassTile(x, y));
                }
            }
        }
    }

    public AbstractTile getTileAt(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height) {
            return null;
        }
        return tiles[x][y];
    }

    public void setTile(int x, int y, AbstractTile tile) {
        if (x >= 0 && y >= 0 && x < width && y < height) {
            tiles[x][y] = tile;
        } else {
            System.out.println("Tile out of bounds: " + x + ", " + y);
        }
    }
}
