package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 60;
    private static final int HEIGHT = 50;

    private static final int HEX_SIZE = 3;

    private static final long SEED = 2873123;
    private static final Random RANDOM = new Random(SEED);

    // let's assume (x,y) is the bottom left point
    public static void addHexagon(int sideLen,int x,int y,TETile[][] tiles,TETile tt) {
        // 1、adds a hexagon of side length s
        // 2、to a given position in the world.

        int midRectH = sideLen * 2; // hexagon max height && middle rectangle height
        int midRectW = sideLen; // middle rectangle width
        int maxPadding = sideLen - 1; // max padding size

        // draw the middle rectangle
        for(int i=x;i<x+midRectW;i++) {
            // from left to right
            drawRectHeightLine(midRectH,i,y,tiles,tt);
        }

        // draw the padding
        // draw maxPadding times
        for(int i=1;i<=maxPadding;i++) {
            int lineHeight = midRectH-(i*2); // padding line height
            int paddingYaxis = y + i; // padding y position
            // left side
            drawRectHeightLine(lineHeight,x-i,paddingYaxis,tiles,tt);
            // right side
            drawRectHeightLine(lineHeight,x+midRectW-1+i,paddingYaxis,tiles,tt);
        }



    }

    public static void drawRectHeightLine(int midRectH,int x,int y,TETile[][] tiles,TETile tt) {
        // draw rows of equal length
        for(int t=y;t<y+midRectH;t++) {
            // from bottom to top
            tiles[x][t] = tt;
        }
    }

    public static void drawHexagonLine(int height,TETile[][] tiles,int x,int y) {
        // this func is to draw hexagon to form line
        int RectLen = HEX_SIZE * 2;
        // random generate Graphics
        TETile tt = randomTile();

        // from bottom to top
        for(int i=0;i<height;i+=1) {
            addHexagon(HEX_SIZE,x,y,tiles,tt);
            y += RectLen;
            tt = randomTile();
        }

    }

    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(5);
        switch (tileNum) {
            case 0: return Tileset.WATER;
            case 1: return Tileset.FLOWER;
            case 2: return Tileset.MOUNTAIN;
            case 3: return Tileset.SAND;
            default: return Tileset.GRASS;
        }

    }


    public static void main(String[] args) {
        // initialize the tile rendering engine with a window of size WIDTH x HEIGHT
        // it is like a pen knowing how to draw
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH,HEIGHT);

        // initialize tiles
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }

//        draw hexagon
//        addHexagon(3,10,10,world);

        /** draw big hexagon */

        // draw the middle part
        int mx = WIDTH / 2 - 1;
        int my = 1;
        drawHexagonLine(5,world,mx,my);

        // draw the padding part
        for(int i=1;i<=2;i++) {

            int yH = my + HEX_SIZE * i;
            // draw left side
            int xLeft = mx - (HEX_SIZE * 2 - 1)* i;
            drawHexagonLine(5-i,world,xLeft,yH);

            // draw right side
            int xRight = mx + (HEX_SIZE * 2 - 1) * i;
            drawHexagonLine(5-i,world,xRight,yH);
        }


    // draws the world to the screen
        ter.renderFrame(world);
    }

}
