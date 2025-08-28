package byow.Core.Components;


import byow.Core.Point;
import byow.Core.Variables;
import byow.Core.World;
import byow.TileEngine.Tileset;

import java.util.*;

public class Road {

    // dir distance is 2 to make sure the next point position is odd
    private final Point EAST = new Point(1,0);
    private final Point WEST = new Point(-1,0);
    private final Point NORTH = new Point(0,1);
    private final Point SOUTH = new Point(0,-1);
    private final Point[] POSSIBLE_DIR = new Point[]{EAST,WEST,NORTH,SOUTH};

    // Control the twists and turns of the maze
    private final int WindingPercent = 35;

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

        // DFS (Flood fill)
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
                Point adjacentCell = cellPoint.add(dir); // Point goes 1 step
                Point nextCell = adjacentCell.add(dir); // Point goes 2 step

                carve(adjacentCell,world);
                carve(nextCell,world);

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

        int xNext = x + dx*2;
        int yNext = y + dy*2;

        // board check
//        if(xNext > 0 && xNext < world.getWidth() && yNext > 0 && yNext < world.getHeight())
        if(world.isBound(xNext,yNext)) {
            return world.getTiles()[xNext][yNext] == Tileset.WALL;
        }

        return false;
    }

    /** connect Room and Road */


    private static class RegionData {
        int[][] regionMap; // record the regions
        int regionCount; // regions number

        RegionData(int[][] regionMap, int regionCount) {
            this.regionMap = regionMap;
            this.regionCount = regionCount;
        }
    }

    // Flood fill to label all the region
    private RegionData findAndLabelRegions(World world) {
        int width = world.getWidth();
        int height = world.getHeight();
        int[][] regionMap = new int[width][height];
        int regionCount = 0;

        for(int i=0; i<width; i++) {
            for(int j=0; j<height; j++) {
                // If we find an un-scanned floor/room tile and it is a new region
                if(world.isIssue(i,j) && regionMap[i][j] == 0) {
                    regionCount++;
                    floodFillBfs(i,j,regionMap,regionCount,world);
                }
            }
        }
        return new RegionData(regionMap,regionCount);
    }

    private void floodFillBfs(int x, int y, int[][] regionMap, int regionCount,World world) {
        Deque<Point> deque = new ArrayDeque<>();
        deque.add(new Point(x,y));

        regionMap[x][y] = regionCount;
        while (!deque.isEmpty()) {
            Point curPoint = deque.poll();
            for(Point dir: POSSIBLE_DIR) {
                Point nP = curPoint.add(dir);
                int nX = nP.getX();
                int nY = nP.getY();
                if(world.isBound(nX,nY) && world.isIssue(nX,nY) && regionMap[nX][nY] == 0) {
                    deque.add(nP);
                    regionMap[nX][nY] = regionCount;
                }
            }
        }
    }

    public void connectRegions(World world, Variables variables) {
        RegionData regionData = findAndLabelRegions(world);
        int[][] regionMap = regionData.regionMap;
        int totalCount = regionData.regionCount;

        if(totalCount <= 1) {
            return; // already connected
        }

        // connector: (point:regions it connect)
        HashMap<Point,Set<Integer>> connectorToRegionsMap = findConnector(world,regionMap);
        List<Point> connectors = new ArrayList<>(connectorToRegionsMap.keySet());

        List<Integer> merged = new ArrayList<>();
        List<Integer> openRegions = new ArrayList<>();

        for(int i=0; i<=totalCount; i++) {
            merged.add(i);
            if(i > 0) {
                // region id 0 is an invalid value
                openRegions.add(i);
            }
        }

        // main loop to merge
        // if open region exist (1 mean already merge together)
        while (openRegions.size() > 1) {
            // Pick a random connector
            Point connector = connectors.get(variables.getRANDOM().nextInt(connectors.size()));
            carve(connector,world);

            // neighbor region ids
            Set<Integer> regionsToMerge = new HashSet<>();
            // go thorough the regions that the connector connects
            for (int regionID : connectorToRegionsMap.get(connector)) {
                // find the ultimate representative of this region.
                // if merge region, then the merged id will change
                // so it is important here to get region id from merged
                regionsToMerge.add(merged.get(regionID));
            }

            // if regions size more than 1
            // mean that exist more than 2 regions need to connect
            if(regionsToMerge.size() > 1) {
                Iterator<Integer> it = regionsToMerge.iterator();
                int dest = it.next(); // the goal region
                while (it.hasNext()) {
                    int sourse = it.next(); // the other region
                    for(int i=1; i<=totalCount; i++) {
                        // region id begin from 1
                        if(merged.get(i) == sourse) {
                            merged.set(i,dest);
                        }
                    }
                    openRegions.remove(Integer.valueOf(sourse));
                }
            }

            // remove connector which has been invalid
            connectors.removeIf(p-> {
                // set is Non-repeatable, mean that it can't contain the same value
                Set<Integer> surroundingRegions = new HashSet<>();
                for(int regionID: connectorToRegionsMap.get(p)) {
                    // if regions have been merged;
                    // merged get the same, so it can remove the connectors
                    surroundingRegions.add(merged.get(regionID));
                }
                return surroundingRegions.size() <= 1;
            });

        }

    }


    public HashMap<Point,Set<Integer>> findConnector(World world, int[][] regionMap) {
        int worldWidth = world.getWidth();
        int worldHeight = world.getHeight();

        HashMap<Point,Set<Integer>> connectors = new HashMap<>();
        Set<Integer> neighboringRegions;
        for(int i=1; i<worldWidth; i++) {
            for(int j=1; j<worldHeight; j++) {
                if(world.isNothing(i,j)) {
                    // if it is Nothing
                    Point point = new Point(i,j);
                    // find its neighbor regions, i.e. ROOM or Floor
                    neighboringRegions = isConnector(point,regionMap,world);
                    if(neighboringRegions.size() >= 2) {
                        // if exists more than 2  it is a good connector
                        // record: (point:regions(which it connect))
                        connectors.put(point,neighboringRegions);
                    }
                }
            }
        }

        return connectors;
    }

    public Set<Integer> isConnector(Point point,int[][] regionMap,World world) {
        Set<Integer> neighboringRegions = new HashSet<>();
        for(Point dir: POSSIBLE_DIR) {
            Point nP = point.add(dir);
            int nX = nP.getX();
            int nY = nP.getY();
            if(!world.isBound(nX,nY)) {
                continue;
            }
            int regionID = regionMap[nX][nY];
            // the neighbor is not NOTHING
            if(regionID > 0) {
                neighboringRegions.add(regionID);
            }
        }
        return neighboringRegions;
    }


    public void removeDeadEnd(World world) {
        int width = world.getWidth();
        int height = world.getHeight();

        for(int i=1; i<width; i++) {
            for(int j=1; j<height; j++) {
                Point point = new Point(i,j);
                removeDeadEndHelper(world,point);
            }
        }
    }

    public void removeDeadEndHelper(World world, Point point) {
        Queue<Point> queue = new ArrayDeque<>();
        queue.add(point);

        while (!queue.isEmpty()) {
            Point curPoint = queue.poll();
            if(isDeadEnd(world,curPoint)) {
                for(Point dir: POSSIBLE_DIR) {
                    Point nP = curPoint.add(dir);
                    int nx = nP.getX();
                    int ny = nP.getY();
                    if(!world.isBound(nx,ny)) {
                        continue;
                    }
                    if(world.isFloor(nx,ny)) {
                        queue.add(nP);
                    }
                }
                // remove dead end
                carveDeadEnd(world,curPoint);
            }
        }
    }

    private void carveDeadEnd(World world,Point point) {
        int nx = point.getX();
        int ny = point.getY();
        world.getTiles()[nx][ny] = Tileset.NOTHING;
    }

    public boolean isDeadEnd(World world, Point point) {
        int count = 0;
        for(Point dir: POSSIBLE_DIR) {
            Point nP = point.add(dir);
            int nX = nP.getX();
            int nY = nP.getY();
            if(!world.isBound(nX,nY)) {
                continue;
            }
            if(world.isNothing(nX,nY)) {
                count++;
            }
        }

        // if the tiles surrounded with 3 nothing
        // it is a dead end
        return count == 3;

    }




}
