package battleship;

import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

public class BattleShipViewCLI implements Observer {

    private static final int ROWSIZE = 10;
    private static final int COLUMNSIZE = 10;
    private static final String EMPTY = ".", SHOT = "X", MISS = "M";

    private String[][] grid = new String[ROWSIZE][COLUMNSIZE];
    private String[] yValues = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
    private boolean hit;
    private Coordinates attackCoordinates;

    private BattleShipController controller;
    private BattleShipModel model;

    /**
     * Start new CLI game
     */
    public void newGameCLI() {
        Scanner kb = new Scanner(System.in);
        model = new BattleShipModel();
        controller = new BattleShipController(model);
        controller.setViewCLI(this);
        model.addObserver(this);

        //select type of game
        int option = -1;
        boolean validOption = false;
        while (!validOption) {
            System.out.println("1: New Default Game");
            System.out.println("2: New Random Game");
            System.out.println("3: Load file");
            option = kb.nextInt();
            switch (option) {
                case 1:
                    controller.initialise(1);
                    createGrid();
                    printGrid();
                    validOption = true;
                    break;
                case 2:
                    controller.initialise(2);
                    createGrid();
                    printGrid();
                    validOption = true;
                    break;
                case 3:
                    validOption = loadGame();
                    break;
                default:
                    System.out.println("Invalid option");
                    break;
            }
        }
        //play game
        while (option != 0) {
            menu();
            option = kb.nextInt();
            switch (option) {
                case 1:
                    System.out.println("Select attack Coordinates");
                    String point = kb.next();
                    if (point.length() >= 2 && point.length() <= 3) {
                        String stringY = point.substring(0, 1).toUpperCase();
                        setAttackCoordinates(new Coordinates((Integer.parseInt(point.substring(1, point.length())) - 1), Arrays.asList(yValues).indexOf(stringY)));
                        if (!controller.positionTried(getAttackCoordinates())) {
                            controller.attack(getAttackCoordinates());
                            if (controller.winGame()) {
                                System.out.println("\nYou Won!!! It took you " + controller.getCoordinatesesTried().size() + " moves \n");
                                option = 0;
                            }
                        }
                        printGrid();
                    } else {
                        System.out.println("Invalid Coordinates ");
                    }
                    break;
                case 2:
                    controller.initialise(1);
                    createGrid();
                    printGrid();
                    break;
                case 3:
                    controller.initialise(2);
                    createGrid();
                    printGrid();
                    break;
                case 4:
                    boolean validFile = false;
                    while (!validFile) {
                        validFile = loadGame();
                    }
                    break;
                case 5:
                    System.out.println("save to file");
                    controller.saveGame();
                    break;
                case 6:
                    printGrid();
                    break;
                case 0:
                    System.out.println("Goodbye");
                    break;
                default:
                    System.out.println("Invalid option");
                    break;
            }
        }
    }

    /**
     * Print menu items
     */
    private void menu() {
        System.out.println("\nPlease enter an option");
        System.out.println("1: Select attack Coordinates");
        System.out.println("2: New Default Game");
        System.out.println("3: New Random Game");
        System.out.println("4: Load file");
        System.out.println("5: Save to file");
        System.out.println("6: Print Grid");
        System.out.println("0: Quit Program \n");
    }

    /**
     * Prompt user to enter name of text file and return true text file exists.
     *
     * @return true if text file exists
     */
    private boolean loadGame() {
        Scanner kb = new Scanner(System.in);
        Boolean validInput = false;
        String fileName = " ";
        while (!validInput) {
            System.out.println("Enter a txt file ");
            fileName = kb.next();
            if (fileName.contains(".")) {
                if (fileName.substring(fileName.lastIndexOf(".") + 1).equals("txt")) {
                    validInput = true;
                }
            }
        }
        boolean validFile = controller.loadGame(fileName);
        if (validFile) {
            populateLoadGame();
        }
        return validFile;
    }

    /**
     * Create 10x10 grid and assign the EMPTY value to each element
     */
    private void createGrid() {
        for (int row = 0; row < ROWSIZE; row++) {
            for (int column = 0; column < COLUMNSIZE; column++) {
                grid[column][row] = EMPTY;
            }
        }
    }

    /**
     * Print the entire grid
     */
    private void printGrid() {
        System.out.print("  ");
        for (int i = 1; i < 11; i++) {
            System.out.print(i + " ");
        }
        for (int row = 0; row < ROWSIZE; row++) {
            System.out.print("\n" + yValues[row] + " ");
            for (int column = 0; column < COLUMNSIZE; column++) {
                System.out.print(grid[column][row] + " ");
            }
        }
        System.out.println(" ");
    }

    /**
     * Populate the grid with text file data. If coordinate tried then the grid
     * position will be assigned a string value of X for hit and M for miss
     */
    private void populateLoadGame() {
        createGrid();
        if (controller.getCoordinatesesTried() != null) {
            for (Coordinates c : controller.getCoordinatesesTried()) {
                if (c.isHit()) {
                    grid[c.getX()][c.getY()] = SHOT;
                } else if (!c.isHit()) {
                    grid[c.getX()][c.getY()] = MISS;
                }
            }
        }
        printGrid();
    }

    /**
     * Update the view using Observer
     *
     * @param o
     * @param o1
     */
    @Override
    public void update(Observable o, Object o1) {
        if (o instanceof BattleShipModel) {
            setHit((Boolean) o1);
        }
        if (isHit()) {
            grid[getAttackCoordinates().getX()][getAttackCoordinates().getY()] = SHOT;
        } else {
            grid[getAttackCoordinates().getX()][getAttackCoordinates().getY()] = MISS;
        }
    }

    /**
     * @return attacked coordinates
     */
    public Coordinates getAttackCoordinates() {
        return attackCoordinates;
    }

    /**
     * @param attackCoordinates coordinates which were selected
     */
    public void setAttackCoordinates(Coordinates attackCoordinates) {
        this.attackCoordinates = attackCoordinates;
    }

    /**
     * @return boolean value hit
     */
    public boolean isHit() {
        return hit;
    }

    /**
     * set the boolean value of hit
     *
     * @param hit
     */
    public void setHit(boolean hit) {
        this.hit = hit;
    }
}
