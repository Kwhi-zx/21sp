package byow.Core;


import java.util.Objects;

/**
 *  This is a class for position
 * */
public class Point implements Comparable<Point>{
    private int x;
    private int y;

    public Point() {

    }

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }


    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Point add(Point p1) {
        Point nP = new Point();

        int x1 = p1.getX();
        int y1 = p1.getY();

        int x2 = this.getX();
        int y2 = this.getY();

        nP.setX(x1+x2);
        nP.setY(y1+y2);

        return nP;
    }

    // implements comparable to use sort
    @Override
    public int compareTo(Point p) {
        if(this.x != p.x) {
            return Integer.compare(this.x,p.x);
        }
        return Integer.compare(this.y,p.y);
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;
        Point point = (Point) obj;

        return x == point.x && y == point.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x,y);
    }
}
