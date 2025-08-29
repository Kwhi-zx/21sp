package byow.Core.Character;

import byow.Core.Point;
import byow.Core.Variables;
import byow.Core.World;
import byow.TileEngine.Tileset;

public class AVATAR {
    private final Kwhi kwhi;

    public AVATAR() {
        kwhi = new Kwhi(0,0);
    }

    public void setKwhi(World world, Variables variables) {
        Point point = randomPosition(world,variables);
        int kx = point.getX();
        int ky = point.getY();
        kwhi.setX(kx);
        kwhi.setY(ky);
        world.getTiles()[kx][ky] = Tileset.AVATAR;
    }

    public Point randomPosition(World world, Variables variables) {
        int width = world.getWidth();
        int height = world.getHeight();
        int x = variables.getRANDOM().nextInt(width);
        int y = variables.getRANDOM().nextInt(height);

        while(!world.isIssue(x,y)) {
            x = variables.getRANDOM().nextInt(width);
            y = variables.getRANDOM().nextInt(height);
        }
        return new Point(x,y);
    }

    public void kMovement(String input, World world) {
        switch (input) {
            case "w","W": kwhi.goUp(world);
            case "s","S": kwhi.goDown(world);
            case "a","A": kwhi.goLeft(world);
            case "d","D": kwhi.goRight(world);
        }
        world.getTiles()[kwhi.getX()][kwhi.getY()] = Tileset.AVATAR;
    }
}
