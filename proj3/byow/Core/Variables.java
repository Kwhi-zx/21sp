package byow.Core;

import byow.Core.Character.AVATAR;
import byow.Core.Components.Road;
import byow.Core.Components.Room;
import byow.Core.Components.Wall;

import java.util.Random;

public class Variables {

    private World world;
    private Room room;
    private Wall wall;
    private Road road;
    private AVATAR avatar;
    private Random RANDOM;

    public Variables(long seed) {
        world = new World(Engine.WIDTH,Engine.HEIGHT);
        room = new Room();
        wall = new Wall();
        road = new Road();
        avatar = new AVATAR();
        RANDOM = new Random(seed);
    }


    public void initializeTheWorld() {
        world.initialize();
        room.createRooms(world,this);
        wall.createWall(world);
        road.createMaze(world,this);
        road.connectRegions(world,this);
        road.removeDeadEnd(world);
        wall.fixNewWall(world);
        avatar.setKwhi(world,this);

    }

    public World getWorld() {
        return world;
    }
    public AVATAR getAvatar() {
        return this.avatar;
    }

    public Random getRANDOM() {
        return RANDOM;
    }




}
