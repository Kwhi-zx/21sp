package byow.Core.Components;


import byow.Core.Point;
import byow.Core.Variables;
import byow.Core.World;
import byow.TileEngine.Tileset;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class Road {

    // dir distance is 2 to make sure the next point position is odd
    private final Point EAST = new Point(2,0);
    private final Point WEST = new Point(-2,0);
    private final Point NORTH = new Point(0,2);
    private final Point SOUTH = new Point(0,-2);
    private final Point[] POSSIBLE_DIR = new Point[]{EAST,WEST,NORTH,SOUTH};

    // Control the twists and turns of the maze
    private final int WindingPercent = 50;

    public Road() {

    }

    public void createMaze(World world, Variables variables) {
        int worldHeight = world.getHeight();
        int worldWidth  = world.getWidth();

        for(int i=1; i<worldWidth; i += 2) {
            for(int j=1; j<worldHeight; j += 2) {
                if(world.isWall(i,j)) {
                    // find the Wall tile
                    // (i,j) is the start point
                    Point pos = new Point(i,j);
                    growMaze(world,pos,variables);
                }
            }
        }
    }

    public void growMaze(World world,Point pos,Variables variables) {
        Stack<Point> cells = new Stack<>();
        // record the last dir
        Point lastDir = new Point(0,0);

        // carve the start pos
        carve(pos,world);
        cells.push(pos);

        // DFS
        while(!cells.isEmpty()) {
            Point cellPoint = cells.peek();

            // check all directions can grow
            Set<Point> unmadeCells = new HashSet<>();
            for(Point dir : POSSIBLE_DIR) {
                // go thorough four direction
                if(isCarve(cellPoint,world,dir)) {
                    // if this direction can grow, record it
                    unmadeCells.add(dir);
                }
            }

            if (!unmadeCells.isEmpty()) {
                Point dir;
                if(unmadeCells.contains(lastDir) && variables.getRANDOM().nextInt(100) > WindingPercent) {
                    dir = lastDir;
                } else {
                    // change another direction
                    Point[] unmadeArray = unmadeCells.toArray(new Point[0]);
                    int randomIndex = variables.getRANDOM().nextInt(unmadeCells.size());
                    dir = unmadeArray[randomIndex];
                }

                // connect the maze
                // the GrowStep is 2
                Point nextCell = cellPoint.add(dir);
                carve(nextCell,world);
                carve(cellPoint.mid(nextCell),world);

                cells.add(nextCell);
                lastDir = dir;

            }else {
                cells.pop();
                lastDir = new Point(0,0);
            }


        }

    }

    public void carve(Point point,World world) {
        int x = point.getX();
        int y = point.getY();
        world.getTiles()[x][y] = Tileset.FLOOR;
    }

    public boolean isCarve(Point point,World world,Point dir) {
        // position
        int x = point.getX();
        int y = point.getY();

        // direction
        int dx = dir.getX();
        int dy = dir.getY();

        int xNext = x + dx;
        int yNext = y + dy;

        // board check
        if(xNext > 0 && xNext < world.getWidth() && yNext > 0 && yNext < world.getHeight()) {
            return world.getTiles()[xNext][yNext] == Tileset.WALL;
        }

        return false;
    }



}
