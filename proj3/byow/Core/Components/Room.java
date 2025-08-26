package byow.Core.Components;

import byow.Core.*;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import static java.lang.Math.min;

public class Room {

    // try how many times to generate rooms
    private static final int TIMES = min(Engine.WIDTH,Engine.HEIGHT) / 2;

    // minimum width & height of a room, must be odd
    private static final int ROOM_MIN_WIDTH = 3;
    private static final int ROOM_MIN_HEIGHT = 3;
    private static final int TILE_GAP = 2;

    public void createRooms(World world, Variables variables) {
        for(int i=0; i< TIMES; i++) {
            createRoom(world,variables);
        }
    }


    public void createRoom(World world, Variables variables) {
        // room's width and height; must be odd
        int width = variables.getRANDOM().nextInt(3) * 2 + ROOM_MIN_WIDTH; // 0,2,4 + xx = 3,5,7
        int height = variables.getRANDOM().nextInt(3) * 2 + ROOM_MIN_HEIGHT;

        // room's start point (position); set it the left bottom one
        int x = world.getRandomX(width,variables);
        int y = world.getRandomY(height,variables);

        // Inflation: to leave a tile gap
        int fw = width + TILE_GAP; // 5,7,9
        int fh = height + TILE_GAP; // 5,7,9
        // iterate times
        int count = 0;

        // check if overlap other rooms
        while(!world.isNothing(x,y) || isRoomOverlap(world,x,y,fw,fh)) {
            x = world.getRandomX(width,variables);
            y = world.getRandomY(height,variables);

            count++;
            if(count == TIMES) {
                return;
            }
        }


        createRoomHelper(world,x,y,width,height,fw,fh);
    }

    public static void createRoomHelper(World world, int x, int y, int w, int h, int fw, int fh) {
        int ex = x - 1;
        int ey = y - 1;

        // inflation
        for(int i=ex; i< ex+fw; i++) {
            for(int j=ey; j< ey+fh; j++) {
                world.getTiles()[i][j] = Tileset.ROOMGAP;
            }
        }

        // exactly Room
        for(int i=x; i< x+w; i++) {
            for(int j=y; j< y+h; j++) {
                world.getTiles()[i][j] = Tileset.ROOM;
            }
        }


    }

    public static boolean isRoomOverlap(World world,int x, int y, int fw, int fh) {
        int ex = x - 1;
        int ey = y - 1;
        // check if there is room exist in the position
        for(int i=ex; i< ex+fw; i++) {
            for(int j=ey; j< ey+fh; j++) {
                if(world.isRoom(i,j) || world.isTileGap(i,j)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void main(String[] args) {

        TERenderer ter = new TERenderer();
        ter.initialize(81,61);

        World world = new World(81,61);
        world.initialize();
        Variables variables = new Variables(114514);
        Room room = new Room();
        Wall wall = new Wall();
        Road road = new Road();
        room.createRooms(world,variables);
        wall.createWall(world,variables);
        road.createMaze(world,variables);

        ter.renderFrame(world.getTiles());
    }



}
