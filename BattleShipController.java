package battleship;

import java.io.FileNotFoundException;
import java.util.List;

public class BattleShipController {

    private BattleShipModel model;
    private BattleShipViewGUI viewGUI;
    private BattleShipViewCLI viewCLI;

    public BattleShipController(BattleShipModel model) {
        this.model = model;
    }

    public void setViewGUI(BattleShipViewGUI viewGUI) {
        this.viewGUI = viewGUI;
    }

    public void setViewCLI(BattleShipViewCLI viewCLI) {
        this.viewCLI = viewCLI;
    }

    /**
     * create new Default or Random game
     *
     * @param typeOfGame the type of Game
     */
    public void initialise(int typeOfGame) {
        model.initialise(typeOfGame);
    }

    /**
     * Checks if the position has already been tried it wont all the same
     * position to be attack (disabling the coordinates)
     *
     * @param points coordinates selected by user
     * @return true if the points have been previously tried
     */
    public boolean positionTried(Coordinates points) {
        boolean alreadyTried = false;
        for (Coordinates c : getCoordinatesesTried()) {
            if (c.getX() == points.getX() && c.getY() == points.getY()) {
                alreadyTried = true;
            }
        }
        return alreadyTried;
    }

    /**
     * Attack selected coordinates
     *
     * @param coordinates coordinates selected by user
     * @return true if a ship was hit
     */
    public boolean attack(Coordinates coordinates) {
        return model.attack(coordinates);
    }

    /**
     * Load game from text file
     *
     * @param fileName String name of the text file
     * @return true if file was loaded
     */
    public boolean loadGame(String fileName) {
        try {
            model.loadGame(fileName);
            return true;
        } catch (FileNotFoundException ex) {
            System.out.println("File Not Found");
            return false;
        }
    }

    /**
     * Save data to text file
     */
    public void saveGame() {
        model.saveGame();
    }

    /**
     * @return list of all ship
     */
    public List<Ship> getShipList() {
        return model.getShipList();
    }

    /**
     * @return List of all the coordinates tried
     */
    public List<Coordinates> getCoordinatesesTried() {
        return model.getCoordinatesesTried();
    }

    /**
     * @return true if all ships are sunk
     */
    public boolean winGame() {
        int counter = 0;
        for (Ship ship : getShipList()) {
            if (!ship.isSunk()) {
                counter += 1;
            }
        }
        return counter == 0 ? true : false;
    }

    /**
     * @return the number of not sunk ships
     */
    public int numberOfActiveShips() {
        int counter = 0;
        for (Ship ship : getShipList()) {
            if (!ship.isSunk()) {
                counter += 1;
            }
        }
        return counter;
    }
}
