package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class World {
    private int width;
    private int height;
    TETile[][] tiles;

    public World(int w,int h) {
        this.width = w;
        this.height = h;
        tiles = new TETile[w][h];
    }

    public TETile[][] getTiles() {
        return this.tiles;
    }

    public void initialize() {
        fillWithNothing();
    }

    private void fillWithNothing() {
        for(int i=0; i<width; i++) {
            for(int j=0; j<height; j++) {
                tiles[i][j] = Tileset.NOTHING;
            }
        }
    }

    // generate random position X
    // X is odd and value range: (1,width-w-1)
    public int getRandomX(int w, Variables variables) {
        return variables.getRANDOM().nextInt((width - w) / 2) * 2 + 1;
    }

    public int getRandomY(int h, Variables variables) {
        return variables.getRANDOM().nextInt((height - h) / 2) * 2 + 1;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public boolean isNothing(int x, int y) {
        return tiles[x][y] == Tileset.NOTHING;
    }

    public boolean isRoom(int x, int y) {
        return tiles[x][y] == Tileset.ROOM;
    }

    public boolean isWall(int x, int y) {

        return tiles[x][y] == Tileset.WALL;
    }

    public boolean isFloor(int x, int y) {
        return tiles[x][y] == Tileset.FLOOR;
    }

    public boolean isIssue(int x, int y) {
        return tiles[x][y] == Tileset.ROOM || tiles[x][y] == Tileset.FLOOR;
    }

    public boolean isBound(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public boolean isUnit(int x, int y) {
        return tiles[x][y] == Tileset.ROOM || tiles[x][y] == Tileset.FLOOR || tiles[x][y] == Tileset.LOCKED_DOOR;
    }


}
