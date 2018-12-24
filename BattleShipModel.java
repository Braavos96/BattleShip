package battleship;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BattleShipModel extends Observable {

    private List<Ship> shipList = new ArrayList<>();
    private List<Coordinates> coordinatesesTried = new ArrayList<>();

    public BattleShipModel() {
    }

    /**
     * Clear any instance of any previous games and creates a new Default or
     * Random game depending on the typeOfGame value
     *
     * @param typeOfGame the type of Game
     * @pre. typeOfGame is equal to 1 or 2
     * @post. Default or Random game is started depending on typeOfGame value
     * @return void
     */
    public void initialise(int typeOfGame) {
        shipList.clear();
        coordinatesesTried.clear();
        createShips(typeOfGame);
    }

    /**
     * Creates ships depending on the type of Game. Both type of games will
     * contain one ship each of length 3,4 & 5 Default game will contain two
     * ships of length 2. Random game will contain a random number of ships
     * between 0 and 4 of length 1 ships and a random number of ships between 0
     * and 3 of length 2
     *
     * @param typeOfGame the type of game
     * @pre. typeOfGame is equal to 1 or 2
     * @post. Number of ships are placed depending on typeOfGame value
     * @return void
     */
    private void createShips(int typeOfGame) {
        List<Ship> ships = new ArrayList<>();

        for (int i = 3; i < 6; i++) {
            ships.add(new Ship(i));
        }

        if (typeOfGame == 1) {//Default Game
            for (int i = 0; i < 2; i++) {
                ships.add(new Ship(2));
            }
        } else if (typeOfGame == 2) {//Random Game
            Random random = new Random();
            for (int i = 0; i < random.nextInt(3); i++) {
                ships.add(new Ship(2));
            }

            for (int i = 0; i < random.nextInt(4); i++) {
                ships.add(new Ship(1));
            }
        }

        addToGrid(ships);
    }

    /**
     * The ships in List ships will be assigned coordinates and added to the
     * board
     *
     * @param ships list of ships to be added to the board
     * @pre. ships Array List size is greater than 3 and less than 10
     * @post. ships Array List is added to the shipList Array List
     * @return void
     */
    private void addToGrid(List<Ship> ships) {
        for (Ship ship : ships) {
            if (ships.size() >= 3 && ships.size() <= 10) {
                ship.setPosition(shipList);
                shipList.add(ship);
            }
        }
    }

    /**
     * User selects coordinates on the board and this method checks if there is
     * a ship in that position. If there is a ship occupying that space then the
     * ship hit counter will increment. The try will be added to the
     * coordinatesesTried List for save/load game
     *
     * @param points coordinates selected by the user to check if a ship exists
     * in that position
     * @pre. points are in range of 0 and 9
     * @post. check if ship has been hit if it has then increase hit by 1
     * @return boolean
     */
    public boolean attack(Coordinates points) {
        boolean hit = false;
        for (int i = 0; i < shipList.size(); i++) {
            Ship ship = shipList.get(i);
            for (int j = 0; j < ship.getSize(); j++) {
                if (ship.getPosition().get(j).getX() == points.getX() && ship.getPosition().get(j).getY() == points.getY()) {
                    hit = true;
                    ship.setHits();
                    points.setHit(hit);
                    ship.getPosition().get(j).setHit(hit);
                }
            }
        }
        coordinatesesTried.add(points);
        setChanged();
        notifyObservers(hit);
        return hit;
    }

    /**
     * @return List shipList
     */
    public List<Ship> getShipList() {
        return shipList;
    }

    /**
     * @return List coordinatesesTried
     */
    public List<Coordinates> getCoordinatesesTried() {
        return coordinatesesTried;
    }

    /**
     * Exports the shipList and coordinatesesTried List to a text file
     *
     * @pre. shipList Array List is not null
     * @post. shipList and coordinatesesTried Array List printed out as a text
     * file
     * @return void
     */
    public void saveGame() {
        try {
            BufferedWriter fileOut = new BufferedWriter(new FileWriter("myfile.txt"));
            StringBuilder stringBuilder = new StringBuilder();
            for (Ship ship : shipList) {
                // convert ships to String builder to be exported as a text file 
                stringBuilder.append("Ship" + System.getProperty("line.separator"));
                for (int i = 0; i < ship.getPosition().size(); i++) {
                    stringBuilder.append("( " + ship.getPosition().get(i).getX() + " , " + ship.getPosition().get(i).getY() + " ) " + ship.getPosition().get(i).isHit() + System.getProperty("line.separator"));
                }
                stringBuilder.append("END" + System.getProperty("line.separator"));
                fileOut.write(stringBuilder.toString());
                stringBuilder.setLength(0);
            }

            stringBuilder.append(System.getProperty("line.separator") + "Coordinates" + System.getProperty("line.separator"));
            for (Coordinates coordinates : coordinatesesTried) {
                // convert coordinates to String builder to be exported as a text file
                if (!coordinates.isHit()) {
                    stringBuilder.append("( " + coordinates.getX() + " , " + coordinates.getY() + " ) " + coordinates.isHit() + System.getProperty("line.separator"));
                    fileOut.write(stringBuilder.toString());
                    stringBuilder.setLength(0);
                }
            }
            fileOut.close();
        } catch (IOException ex) {
            Logger.getLogger(BattleShipModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Clears any previous progress of a game and loads the data from a text
     * file and populates the Lists shipList and coordinatesesTried
     *
     * @param fileName the name of the file to be loaded from
     * @throws FileNotFoundException
     * @pre. shipList and coordinatesesTried Array List is null
     * @post. shipList and coordinatesesTried Array List populated by loaded
     * text file
     * @return void
     */
    public void loadGame(String fileName) throws FileNotFoundException {
        shipList.clear();
        coordinatesesTried.clear();

        Scanner scan = new Scanner(new FileInputStream(fileName));
        while (scan.hasNext()) {
            String name = scan.next();
            if (!name.equals("Coordinates")) {
                shipList.add(importShips(scan));
            } else {
                importCoordinates(coordinatesesTried, scan);
            }
        }
    }

    /**
     * Import ships and coordinates of ships from text file and create ship
     * objects
     *
     * @param scan read input from a text file
     * @pre. text file contains ships in the correct format
     * @post. create ships from the text file and add them to shipList
     * @return Ship
     */
    private Ship importShips(Scanner scan) {
        List<Coordinates> position = new ArrayList<>();
        int hitCounter = 0;
        importCoordinates(position, scan);
        for (int i = 0; i < position.size(); i++) {
            if (position.get(i).isHit()) {
                hitCounter++;
                coordinatesesTried.add(position.get(i));
            }
        }
        Ship ship = new Ship(position.size(), hitCounter, position, position.size() == hitCounter);
        return ship;
    }

    /**
     * Import coordinates from text file and create coordinates objects
     *
     * @param position List of coordinates
     * @param scan read input from a text file
     * @pre. text file contains coordinates in the correct format
     * @post. create coordinates and add them to position Array List
     * @return void
     */
    private void importCoordinates(List<Coordinates> position, Scanner scan) {
        if (scan.hasNext() && !scan.next().equals("END")) {
            int x = scan.nextInt();
            scan.next();
            int y = scan.nextInt();
            scan.next();
            boolean isShot = scan.nextBoolean();
            Coordinates coordinates = new Coordinates(x, y, isShot);
            position.add(coordinates);
            importCoordinates(position, scan);
        }
    }
}
