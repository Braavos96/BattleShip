package battleship;

import java.util.Scanner;
import javafx.application.Application;

public class Main {

    /**
     * Main method allows user to select which view they would like to use
     *
     * @param args
     */
    public static void main(String[] args) {
        Scanner kb = new Scanner(System.in);
        System.out.println("Select Game Version ");
        System.out.println("1: Battleship CLI");
        System.out.println("2: Battleship GUI");
        boolean validOption = false;
        while (!validOption) {
            int option = kb.nextInt();
            switch (option) {
                case 1:
                    BattleShipViewCLI battleShipViewCLI = new BattleShipViewCLI();
                    battleShipViewCLI.newGameCLI();
                    validOption = true;
                    break;
                case 2:
                    Application.launch(BattleShipViewGUI.class, args);
                    validOption = true;
                    break;
                default:
                    System.out.println("Invalid option");
                    break;
            }
        }
    }
}
