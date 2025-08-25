package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class World {
    private int width;
    private int height;
    TETile[][] tiles;

    public World(int width,int height) {

    }

    public TETile[][] getTiles() {
        return this.tiles;
    }

    // generate random position X
    // X is odd and value range: (1,width-w-1)
    public int getRandomX(int w, Variables variables) {
        return variables.getRANDOM().nextInt((width - w - 1) / 2) * 2 + 1;
    }

    public int getRandomY(int h, Variables variables) {
        return variables.getRANDOM().nextInt((height - h - 1) / 2) * 2 + 1;
    }

    public boolean isNothing(int x, int y) {
        return tiles[x][y] == Tileset.NOTHING;
    }

    public boolean isRoom(int x, int y) {
        return tiles[x][y] == Tileset.ROOM;
    }

    public boolean isTileGap(int x, int y) {
        return tiles[x][y] == Tileset.TILE;
    }

}
