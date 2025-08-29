package byow.Core.Character;

import byow.Core.World;

public class Kwhi {
    private int x;
    private int y;
    private int health;

    public Kwhi(int x, int y) {
        this.x = x;
        this.y = y;
        this.health = 100;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getHealth() {
        return health;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void goUp(World world) {
        if(world.isBound(x,y+1) && world.isUnit(x,y+1)) {
            this.y += 1;
        }
    }

    public void goDown(World world) {
        if(world.isBound(x,y-1) && world.isUnit(x,y-1)) {
            this.y -= 1;
        }
    }

    public void goLeft(World world) {
        if(world.isBound(x-1,y) && world.isUnit(x-1,y)) {
            this.x -= 1;
        }
    }

    public void goRight(World world) {
        if(world.isBound(x+1,y) && world.isUnit(x+1,y)) {
            this.x += 1;
        }
    }
}
