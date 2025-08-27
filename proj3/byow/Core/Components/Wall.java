package byow.Core.Components;


import byow.Core.Variables;
import byow.Core.World;
import byow.TileEngine.Tileset;

public class Wall {

    public Wall() {

    }

    public void createWall(World world, Variables variables) {
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
}
