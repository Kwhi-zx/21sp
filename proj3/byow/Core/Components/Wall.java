package byow.Core.Components;


import byow.Core.Point;
import byow.Core.Variables;
import byow.Core.World;
import byow.TileEngine.Tileset;

public class Wall {

    private final Point DEAST = new Point(1,0);
    private final Point DWEST = new Point(-1,0);
    private final Point DNORTH = new Point(0,1);
    private final Point DSOUTH = new Point(0,-1);
    private final Point EN = new Point(1,1); // east north
    private final Point ES = new Point(1,-1); // east south
    private final Point WN = new Point(-1,1); // west north
    private final Point WS = new Point(-1,-1); // west south
    private final Point[] POSSIBLE_DIR_WALL = new Point[]{DEAST,DWEST,DNORTH,DSOUTH,EN,ES,WN,WS};

    public Wall() {

    }

    public void createWall(World world) {
        int worldHeight = world.getHeight();
        int worldWidth = world.getWidth();

        // fill the odd position
        for(int i=0; i<worldWidth; i++) {
            for(int j=0; j<worldHeight; j++) {
                if(i%2 != 0 && j%2 != 0 && !world.isRoom(i,j)) {
                    world.getTiles()[i][j] = Tileset.WALL;
                }
            }
        }
    }

    public void fixNewWall(World world) {
        int worldWidth = world.getWidth();
        int worldHeight = world.getHeight();

        for(int i=0; i<worldWidth; i++) {
            for(int j=0; j<worldHeight; j++) {
                Point point = new Point(i,j);
                // find the room/floor
                if(world.isIssue(i,j)) {
                    fixWallHelper(world,point);
                }
            }
        }
    }

    private void fixWallHelper(World world, Point point) {
        for(Point dir:POSSIBLE_DIR_WALL) {
            Point np = point.add(dir);
            int nx = np.getX();
            int ny = np.getY();
            if(world.isNothing(nx,ny)) {
                craveWall(world,np);
            }
        }
    }

    private void craveWall(World world, Point point) {
        int x = point.getX();
        int y = point.getY();
        world.getTiles()[x][y] = Tileset.WALL;
    }

}
