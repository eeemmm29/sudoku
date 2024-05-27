package com.example;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleIntegerProperty;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.Node;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SudokuGameController {
    private int easy = 40;
    private int medium = 50;
    private int hard = 60;
    private int currentScore = 0;
    private int currentMistakes = 0;
    private final IntegerProperty currentDifficulty = new SimpleIntegerProperty(easy);
    private String currentDifficultyStr = "Easy";
    private Timeline timeline;
    private int seconds;
    private Cell selectedCell;
    private Cell[][] cells = new Cell[SudokuConstants.GRID_SIZE][SudokuConstants.GRID_SIZE];
    private final CellInputListener listener = new CellInputListener();
    private Puzzle puzzle = new Puzzle();
    
    @FXML private AnchorPane mainPane;
    @FXML private Label scoreLabel;
    @FXML private Label mistakesLabel;
    @FXML private Label difficultyLabel;
    @FXML private Label timeLabel;
    @FXML private ImageView top;
    @FXML private GridPane gridPane;
    @FXML private Button btnChangeDifficulty;

    // Number buttons
    @FXML private Button button_one;
    @FXML private Button button_two;
    @FXML private Button button_three;
    @FXML private Button button_four;
    @FXML private Button button_five;
    @FXML private Button button_six;
    @FXML private Button button_seven;
    @FXML private Button button_eight;
    @FXML private Button button_nine;
    
    // New game button
    @FXML private Button btnNewGame;
    // Hint button
    @FXML private Button hint;
    // Note button
    @FXML private Button note;

    // Overlay
    @FXML private AnchorPane darkOverlay;
    // Choose difficulty menu
    @FXML private AnchorPane chooseDifficulty;
    // difficulty buttons in the menu
    @FXML private Button easyDifficultyButton;
    @FXML private Button mediumDifficultyButton;
    @FXML private Button hardDifficultyButton;

    @FXML
    private void openChooseDifficultyMenu() {
        darkOverlay.setVisible(true);
        chooseDifficulty.setVisible(true);
    }

    @FXML
    private void closeChooseDifficultyMenu() {
        darkOverlay.setVisible(false);
        chooseDifficulty.setVisible(false);
    }

    /**
     * Generate a new puzzle and reset the game board of cells based on the puzzle.
     * You can call this method to start a new game.
     */
    @FXML
    private void newGame() {
        // Temporarily remove listeners from cells
        removeCellListeners();
        // Reset mistakes
        currentMistakes = 0;
        mistakesLabel.setText("0");
        // Reset score
        currentScore = 0;
        scoreLabel.setText("0");

        puzzle.newPuzzle(currentDifficulty.get());
        difficultyLabel.setText(currentDifficultyStr);
        currentScore = 0;
        
        // Initialize all the 9x9 cells, based on the puzzle.
        for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
                cells[row][col].setNumberAndStatus(0, false);
                cells[row][col].setNumberAndStatus(puzzle.numbers[row][col], puzzle.isGiven[row][col]);
                cells[row][col].getStyleClass().clear();
                cells[row][col].getStyleClass().add("text-field-style");
            }
        }

        // Reattach listeners to cells
        addCellListeners();

        // Reset the timer
        seconds = 0;
        timeLabel.setText("00:00:00");
        if (timeline != null) {
            timeline.stop();
        }
        startTimer();

        System.out.println("A new game has been created with difficulty: " + currentDifficultyStr);
    }
    
    private void addCellListeners() {
        for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
                if (cells[row][col].isEditable()) {
                    cells[row][col].textProperty().addListener(listener);
                }
            }
        }
    }

    private void removeCellListeners() {
        for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
                    cells[row][col].textProperty().removeListener(listener);
            }
        }
    }

    @FXML
    private void newGameEasyDifficulty() {
        closeChooseDifficultyMenu();
        currentDifficulty.setValue(easy);
        currentDifficultyStr = "Easy";
        newGame();
    }

    @FXML
    private void newGameMediumDifficulty() {
        closeChooseDifficultyMenu();
        currentDifficulty.setValue(medium);
        currentDifficultyStr = "Medium";
        newGame();
    }

    @FXML
    private void newGameHardDifficulty() {
        closeChooseDifficultyMenu();
        currentDifficulty.setValue(hard);
        currentDifficultyStr = "Hard";
        newGame();
    }

    @FXML
    private void handleHintButtonClick() {
        List<Cell> emptyCells = new ArrayList<>();
        for (int row = 0; row < SudokuConstants.GRID_SIZE; row++) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; col++) {
                if (cells[row][col].isEditable() && cells[row][col].getText().isEmpty()) {
                    emptyCells.add(cells[row][col]);
                }
            }
        }

        if (!emptyCells.isEmpty()) {
            Random random = new Random();
            Cell hintCell = emptyCells.get(random.nextInt(emptyCells.size()));
            int row = hintCell.getRow();
            int col = hintCell.getCol();
            hintCell.setText(String.valueOf(puzzle.numbers[row][col]));
            hintCell.setStatus(CellStatus.CORRECT_GUESS);
            System.out.println("hint has been activated");
            hintCell.getStyleClass().addAll("text-field-style", "hint");
            hintCell.setEditable(false);
            currentScore = Math.max(currentScore - 40, 0);
            scoreLabel.setText(Integer.toString(currentScore));
        }
    }

    public void initialize() {
        
        gridPane.setAlignment(Pos.CENTER);

        for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
                cells[row][col] = new Cell();
                // Set position properties for each cell
                cells[row][col].setPosition(row, col);
                cells[row][col].setEditable(true); // Set all cells as editable initially
                gridPane.add(cells[row][col], col, row); // Add the cell to the gridPane

                // Create final copies of row and col
                final int finalRow = row;
                final int finalCol = col;

                // Set focus listener to update selectedCell
                cells[finalRow][finalCol].focusedProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        selectedCell = cells[finalRow][finalCol];
                    }
                });
            }
        }
        
        gridPane.addEventFilter(KeyEvent.KEY_PRESSED, this::handleArrowNavigation);

        // Setting up number button handlers
        Button[] numberButtons = {button_one, button_two, button_three, button_four, button_five, button_six, button_seven, button_eight, button_nine};
        for (int i = 0; i < numberButtons.length; i++) {
            final int number = i + 1;
            numberButtons[i].setUserData(number);
            numberButtons[i].setOnAction(numberButtonHandler);
        }

        newGame();
    }

    /**
     * Return true if the puzzle is solved
     * i.e., none of the cell have status of TO_GUESS or WRONG_GUESS
     */
    public boolean isSolved() {
        for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
                if (cells[row][col].getStatus() == CellStatus.TO_GUESS || cells[row][col].getStatus() == CellStatus.WRONG_GUESS) {
                    return false;
                }
            }
        }
        return true;
    }

    private void handleArrowNavigation(KeyEvent event) {
        Node source = (Node) event.getSource(); // the GridPane
        Node focused = source.getScene().getFocusOwner();
        if (event.getCode().isArrowKey() && focused.getParent() == source) {
            int row = GridPane.getRowIndex(focused);
            int col = GridPane.getColumnIndex(focused);
            switch (event.getCode()) {
                case LEFT:
                    cells[row][Math.max(0, col - 1)].requestFocus();
                    break;
                case RIGHT:
                    cells[row][Math.min(SudokuConstants.GRID_SIZE - 1, col + 1)].requestFocus();
                    break;
                case UP:
                    cells[Math.max(0, row - 1)][col].requestFocus();
                    break;
                case DOWN:
                    cells[Math.min(SudokuConstants.GRID_SIZE - 1, row + 1)][col].requestFocus();
                    break;
                default:
                    break;
            }
            event.consume();
        }
    }

    private class CellInputListener implements ChangeListener<String> {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

            // Debugging output to verify values
            System.out.println("Old Value: " + oldValue + ", New Value: " + newValue);

            // Do nothing if the new value is the same as the old value
            if (newValue == oldValue) {
                System.out.println("No change in value, skipping processing.");
                return;
            }

            // Get a reference of the Cell that triggers this action event
            Cell sourceCell = (Cell) ((ReadOnlyProperty) observable).getBean();
    
            // Try parsing the text to an integer
            try {
                int numberIn = Integer.parseInt(newValue);
                // Rest of your code handling the parsed integer
            
                /*
                * Check the numberIn against sourceCell.number.
                * Update the cell status sourceCell.status,
                * and re-paint the cell via sourceCell.paint().
                */
                if (numberIn == sourceCell.getNumber()) {
                    sourceCell.setStatus(CellStatus.CORRECT_GUESS);
                    sourceCell.getStyleClass().add("correct-guess");
                    sourceCell.setEditable(false);
                    System.out.println("Correct guess");

                    currentScore += 30;
                } else {
                    sourceCell.setStatus(CellStatus.WRONG_GUESS);
                    sourceCell.getStyleClass().add("wrong-guess");
                    System.out.println("Wrong guess");

                    currentMistakes++;
                    mistakesLabel.setText(Integer.toString(currentMistakes));

                    currentScore = Math.max(currentScore - 10, 0);
                }

                scoreLabel.setText(Integer.toString(currentScore));
                
                // For debugging
                System.out.println("You entered " + numberIn);

                /*
                    * Check if the player has solved the puzzle after this move,
                    * by calling isSolved(). Put up a congratulation JOptionPane, if so.
                    */
                if (isSolved()) {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Congratulations!");
                    alert.setHeaderText(null);
                    alert.setContentText("Puzzle solved!");
                    alert.showAndWait();
                }
            } catch (NumberFormatException e) {
                // Handle the case where the text is not a valid integer
                System.err.println("Invalid input: " + newValue);
            }
        }
    }

    EventHandler<ActionEvent> numberButtonHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if (selectedCell != null && selectedCell.isEditable()) {
                Button sourceButton = (Button) event.getSource();
                int number = (int) sourceButton.getUserData();
                selectedCell.setText(Integer.toString(number));
            }
        }
    };

    private void startTimer() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            seconds++;
            int hours = seconds / 3600;
            int minutes = (seconds % 3600) / 60;
            int secs = seconds % 60;
            timeLabel.setText(String.format("%02d:%02d:%02d", hours, minutes, secs));
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
}
