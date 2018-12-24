package battleship;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Ship {

    private int size, hits;
    private List<Coordinates> shipPoints;
    private boolean sunk;

    public Ship(int size) {
        this.size = size;
        this.hits = 0;
        this.shipPoints = new ArrayList<Coordinates>();
        this.sunk = false;
    }

    public Ship(int size, int hits, List<Coordinates> shipPoints, boolean sunk) {
        this.size = size;
        this.hits = hits;
        this.shipPoints = shipPoints;
        this.sunk = sunk;
    }

    /**
     * @return random coordinates of x and y between 0 and 9
     */
    private Coordinates getCoordinates() {
        return new Coordinates(new Random().nextInt(10), new Random().nextInt(10));
    }

    /**
     * @return random direction of either Vertical or Horizontal
     */
    private String getDirection() {
        String[] directions = new String[]{"Vertical", "Horizontal"};
        return directions[new Random().nextInt(2)];
    }

    /**
     * Assigns the ship with coordinates and direction, then checks if the ship
     * overlaps any other ships on the board. If it does overlap then it clears
     * any instances of coordinates assigned to the ship. This will be looped
     * until the ship is placed within the gird and not overlapping any ships
     *
     * @param shipList List of ships to checked against new ship to check if
     * there is any overlap
     */
    public void setPosition(List<Ship> shipList) {
        positionShip();
        if (shipList.size() != 0) {
            while (checkOverlap(shipList)) {
                shipPoints.clear();
                positionShip();
            }
        }
    }

    /**
     * Assigns the ship with the starting coordinate and a direction. This also
     * checks if the ship is placed within the 10x10 grid
     */
    private void positionShip() {
        Coordinates startPoint = getCoordinates();
        String direction = getDirection();

        if ((startPoint.getY() + (size - 1) < 10 && direction.equals("Vertical"))
                || (startPoint.getX() + (size - 1) < 10 && direction.equals("Horizontal"))) {
            shipPoints.add(startPoint);
            addRemainingCoordinates(shipPoints.get(0), direction);
        } else {
            positionShip();
        }
    }

    /**
     * Adds the remaining coordinates to the ship depending on the direction of
     * the ship
     *
     * @param nextPoint previous point + 1
     * @param direction either Vertical or Horizontal
     */
    private void addRemainingCoordinates(Coordinates nextPoint, String direction) {
        if (shipPoints.size() != size) {
            Coordinates temp = new Coordinates(nextPoint.getX(), nextPoint.getY());
            shipPoints.add(temp);
            if (direction.equals("Vertical")) {
                temp.setY(nextPoint.getY() + 1);
            } else if (direction.equals("Horizontal")) {
                temp.setX(nextPoint.getX() + 1);
            }
            addRemainingCoordinates(temp, direction);
        }
    }

    /**
     * @param shipList List of ships to check if there is any overlap
     * @return true if the ship being added to the board is intersecting another
     * ship at any point
     */
    private boolean checkOverlap(List<Ship> shipList) {
        Boolean overlap = false;
        if (!shipList.isEmpty()) {
            for (int i = 0; i < shipList.size(); i++) {
                Ship otherShips = shipList.get(i);
                for (int j = 0; j < otherShips.size; j++) {
                    for (int k = 0; k < shipPoints.size(); k++) {
                        if ((otherShips.getPosition().get(j).getX() == shipPoints.get(k).getX()
                                && otherShips.getPosition().get(j).getY() == shipPoints.get(k).getY())) {
                            overlap = true;
                        }
                    }
                }
            }
        }
        return overlap;
    }

    /**
     * @return List of ship coordinates
     */
    public List<Coordinates> getPosition() {
        return shipPoints;
    }

    /**
     * @return size of the ship
     */
    public int getSize() {
        return size;
    }

    /**
     * @return number of ship hits
     */
    public int getHits() {
        return hits;
    }

    /**
     * @return true if the ship of the ship and number of hits are the same
     */
    public boolean isSunk() {
        return getSize() == getHits();
    }

    /**
     * Increments the number of hits to the ship by 1
     */
    public void setHits() {
        this.hits += 1;
        if (isSunk()) {
            this.sunk = true;
        }
    }
}
