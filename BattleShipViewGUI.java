package battleship;

import java.io.File;
import java.util.Observable;
import java.util.Observer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class BattleShipViewGUI extends Application implements Observer {

    private static final int GRIDHEIGHT = 40;
    private static final int GRIDWIDTH = 40;
    private static final int ROWSIZE = 11;
    private static final int COLUMNSIZE = 11;
    private static final int WINDOW_WIDTH = 500;
    private static final int WINDOW_HEIGHT = 550;

    private Coordinates attackCoordinates;
    private boolean hit;

    private GridPane board = createGrid();
    private BattleShipController controller;
    private BattleShipModel model;

    @Override
    public void start(Stage primaryStage) {
        model = new BattleShipModel();
        controller = new BattleShipController(model);
        controller.setViewGUI(this);
        model.addObserver(this);

        Scene gameBoard = board(primaryStage);
        board.setVisible(false);

        primaryStage.setTitle("BattleShip!");
        primaryStage.setScene(gameBoard);
        primaryStage.show();
    }

    /**
     * Create a new board with button grid of 10x10 and menu options
     *
     * @param stage
     * @return scene with all objects added
     */
    private Scene board(Stage stage) {
        GridPane root = new GridPane();
        HBox menuOptions = menu(stage);

        GridPane.setConstraints(board, 0, 0);
        GridPane.setConstraints(menuOptions, 0, 1);
        board.setAlignment(Pos.CENTER);
        menuOptions.setAlignment(Pos.CENTER);
        root.getChildren().addAll(board, menuOptions);

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        return scene;
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

        for (Node node : board.getChildren()) {
            if (node instanceof Button) {
                if (board.getRowIndex(node) == getAttackCoordinates().getY() + 1
                        && board.getColumnIndex(node) == getAttackCoordinates().getX() + 1) {
                    Button button = (Button) node;
                    button.setText(isHit() ? "X" : "M");
                    button.setDisable(true);
                    break;
                }
            }
        }

        if (controller.winGame()) {
            gameWon();
        }
    }

    /**
     * Create HBox menu options buttons
     *
     * @param stage
     * @return HBox with all menu options
     */
    private HBox menu(Stage stage) {
        HBox menuButtons = new HBox();

        Button buttonSaveGame = new Button();
        buttonSaveGame.setVisible(false);
        buttonSaveGame.setText("Save Game");
        buttonSaveGame.setOnAction((event) -> {
            controller.saveGame();
        }
        );

        Button buttonNewDefaultGame = new Button();
        buttonNewDefaultGame.setText("New Default Game");
        buttonNewDefaultGame.setOnAction((event) -> {
            cleanGrid();
            controller.initialise(1);
            board.setManaged(true);
            board.setVisible(true);
            buttonSaveGame.setVisible(true);
        }
        );

        Button buttonNewRandomGame = new Button();
        buttonNewRandomGame.setText("New Random Game");
        buttonNewRandomGame.setOnAction((event) -> {
            cleanGrid();
            controller.initialise(2);
            board.setManaged(true);
            board.setVisible(true);
            buttonSaveGame.setVisible(true);
        }
        );

        Button buttonLoadGame = new Button();
        buttonLoadGame.setText("Load Game");
        buttonLoadGame.setOnAction((event) -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Game File");
            fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Text Files", "*.txt"));
            File fileName = fileChooser.showOpenDialog(stage);

            if (fileName != null) {
                controller.loadGame(fileName.getName());
                cleanGrid();
                populateLoadGame();
                board.setManaged(true);
                board.setVisible(true);
                buttonSaveGame.setVisible(true);
            }
        }
        );
        menuButtons.getChildren().addAll(buttonNewDefaultGame, buttonNewRandomGame, buttonLoadGame, buttonSaveGame);
        return menuButtons;
    }

    /**
     * Create 10x10 grid of buttons and labels for x and y
     *
     * @return GridPane with labels and buttons added
     */
    private GridPane createGrid() {
        GridPane grid = new GridPane();
        String[] yValues = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};

        for (int row = 1; row < ROWSIZE; row++) {
            Label lab = new Label(yValues[row - 1]);
            lab.setMinSize(GRIDWIDTH, GRIDHEIGHT);
            lab.setAlignment(Pos.CENTER);
            grid.add(lab, 0, row);
        }

        for (int col = 1; col < COLUMNSIZE; col++) {
            Label lab = new Label(Integer.toString(col));
            lab.setMinSize(GRIDWIDTH, GRIDHEIGHT);
            lab.setAlignment(Pos.CENTER);
            grid.add(lab, col, 0);
        }

        for (int row = 1; row < ROWSIZE; row++) {
            for (int col = 1; col < COLUMNSIZE; col++) {
                Button rec = new Button();
                rec.setMinSize(GRIDWIDTH, GRIDHEIGHT);
                GridPane.setRowIndex(rec, row);
                grid.setMargin(rec, new Insets(1, 1, 1, 1));
                grid.add(rec, col, row);

                rec.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        for (Node node : board.getChildren()) {
                            if (node instanceof Button) {
                                if (node.getBoundsInParent().contains(event.getSceneX(), event.getSceneY())) {
                                    setAttackCoordinates(new Coordinates(GridPane.getColumnIndex(node) - 1, GridPane.getRowIndex(node) - 1));
                                }
                            }
                        }
                        if (!controller.positionTried(getAttackCoordinates())) {
                            controller.attack(getAttackCoordinates());
                        }
                    }
                });
            }
        }
        grid.setManaged(false);
        grid.setAlignment(Pos.TOP_LEFT);
        return grid;
    }

    /**
     * Populate the grid with text file data. If coordinate tried then button
     * will be disable and assigned a string value of X for hit and M for miss
     */
    private void populateLoadGame() {
        for (Node node : board.getChildren()) {
            if (node instanceof Button) {
                for (Coordinates c : controller.getCoordinatesesTried()) {
                    if (board.getRowIndex(node) - 1 == c.getY() && board.getColumnIndex(node) - 1 == c.getX()) {
                        Button buttonGrid = (Button) node;
                        buttonGrid.setDisable(true);
                        buttonGrid.setText(c.isHit() ? "X" : "M");
                    }
                }
            }
        }
    }

    /**
     * Enable all the buttons on the grid and clear of any string values
     */
    private void cleanGrid() {
        for (Node node : board.getChildren()) {
            if (node instanceof Button) {
                ((Button) node).setText("");
                ((Button) node).setDisable(false);
            }
        }
    }

    /**
     * If the game has been won then display an alert dialog to inform the user
     */
    private void gameWon() {
        for (Node node : board.getChildren()) {
            if (node instanceof Button) {
                ((Button) node).setDisable(true);
            }
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("You Won!!!");
        alert.setHeaderText("You Won!!!");
        alert.setContentText("It took you " + controller.getCoordinatesesTried().size() + " moves ");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
            }
        });
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
