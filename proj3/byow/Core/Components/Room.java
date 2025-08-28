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

        // iterate times
        int count = 0;

        // check if overlap other rooms
        while(!world.isNothing(x,y) || isRoomOverlap(world,x,y,width,height)) {
            x = world.getRandomX(width,variables);
            y = world.getRandomY(height,variables);

            count++;
            if(count == TIMES) {
                return;
            }
        }


        createRoomHelper(world,x,y,width,height);
    }

    public static void createRoomHelper(World world, int x, int y, int w, int h) {

        // exactly Room
        for(int i=x; i< x+w; i++) {
            for(int j=y; j< y+h; j++) {
                world.getTiles()[i][j] = Tileset.ROOM;
            }
        }

    }

    public static boolean isRoomOverlap(World world,int x, int y, int w, int h) {

        // check if there is room exist in the position
        for(int i=x; i< x+w; i++) {
            for(int j=y; j< y+h; j++) {
                if(world.isRoom(i,j)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void main(String[] args) {

        TERenderer ter = new TERenderer();
        ter.initialize(61,41);

        World world = new World(61,41);
        world.initialize();
        Variables variables = new Variables(444514);
        Room room = new Room();
        Wall wall = new Wall();
        Road road = new Road();
        room.createRooms(world,variables);
        wall.createWall(world);
        road.createMaze(world,variables);
        road.connectRegions(world,variables);
        road.removeDeadEnd(world);
        wall.fixNewWall(world);

        ter.renderFrame(world.getTiles());

    }



}
