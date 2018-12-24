package battleship;

public class Coordinates {

    private int x, y;
    private boolean hit;//if there was a ship in its position and it is hit

    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
        this.hit = false;
    }

    public Coordinates(int x, int y, boolean hit) {
        this.x = x;
        this.y = y;
        this.hit = hit;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isHit() {
        return hit;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setHit(boolean hit) {
        this.hit = hit;
    }
}
