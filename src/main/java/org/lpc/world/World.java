package org.lpc.world;

import lombok.Getter;
import lombok.Setter;
import org.lpc.world.tiles.Tile;
import org.lpc.world.tiles.floor_tiles.FloorTile;
import org.lpc.world.tiles.wall_tiles.WallTile;

@Getter
@Setter
public class World {
    private Tile[][] tiles;
    private int width, height;

    public World(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new Tile[width][height];
        initWorld();
    }

    private void initWorld() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (x == 0 || y == 0 || x == width - 1 || y == height - 1) {
                    tiles[x][y] = new WallTile(x, y, 32, 32, 1);
                } else {
                    tiles[x][y] = new FloorTile(x, y, 32, 32, 2, 1.0);
                }
            }
        }
    }

    public Tile getTileAt(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height) {
            return null;
        }
        return tiles[x][y];
    }

    public void setTile(int x, int y, Tile tile) {
        if (x >= 0 && y >= 0 && x < width && y < height) {
            tiles[x][y] = tile;
        }
    }
}
