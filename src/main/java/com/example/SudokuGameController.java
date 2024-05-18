package com.example;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.input.KeyCode;
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
    private final IntegerProperty currentDifficulty = new SimpleIntegerProperty(easy);
    private String currentDifficultyStr = "Easy";
    private Timeline timeline;
    private int seconds;
    private Cell selectedCell;
    private Cell[][] cells = new Cell[SudokuConstants.GRID_SIZE][SudokuConstants.GRID_SIZE];
    private Puzzle puzzle = new Puzzle();
    
    @FXML private AnchorPane mainPane;
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

    @FXML
    private void newGameEasyDifficulty() {
        closeChooseDifficultyMenu();
        currentDifficulty.setValue(easy);
        currentDifficultyStr = "Easy";
        newGame(currentDifficulty.get());
    }

    @FXML
    private void newGameMediumDifficulty() {
        closeChooseDifficultyMenu();
        currentDifficulty.setValue(medium);
        currentDifficultyStr = "Medium";
        newGame(currentDifficulty.get());
    }

    @FXML
    private void newGameHardDifficulty() {
        closeChooseDifficultyMenu();
        currentDifficulty.setValue(hard);
        currentDifficultyStr = "Hard";
        newGame(currentDifficulty.get());
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

                // Add key event filter for Backspace
                cells[row][col].addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                    if (event.getCode() == KeyCode.BACK_SPACE) {
                        handleBackspaceKey(cells[finalRow][finalCol]);
                        event.consume();
                    }
                });
            }
        }
        
        gridPane.addEventFilter(KeyEvent.KEY_PRESSED, this::handleArrowNavigation);

        CellInputListener listener = new CellInputListener();
  
        for (int row = 0; row < cells.length; row++) {
            for (int col = 0; col < cells[row].length; col++) {
               if (cells[row][col].isEditable()) {
                  cells[row][col].setOnAction(listener);   // For all editable rows and cols
               }
            }
        }

        // Setting up number button handlers
        button_one.setOnAction(event -> handleNumberButtonClick(1));
        button_two.setOnAction(event -> handleNumberButtonClick(2));
        button_three.setOnAction(event -> handleNumberButtonClick(3));
        button_four.setOnAction(event -> handleNumberButtonClick(4));
        button_five.setOnAction(event -> handleNumberButtonClick(5));
        button_six.setOnAction(event -> handleNumberButtonClick(6));
        button_seven.setOnAction(event -> handleNumberButtonClick(7));
        button_eight.setOnAction(event -> handleNumberButtonClick(8));
        button_nine.setOnAction(event -> handleNumberButtonClick(9));

        newGame(currentDifficulty.get());
        btnNewGame.setOnAction(event -> newGame(currentDifficulty.get()));
        hint.setOnAction(event -> handleHintButtonClick()); // Add hint button handler
    }

    // TODO better handle backspace
    private void handleBackspaceKey(Cell cell) {
        if (cell.isEditable()) {
            cell.setText("");
            cell.setStatus(CellStatus.TO_GUESS);
            cell.getStyleClass().clear();;
            cell.getStyleClass().add("text-field-style");
        }
    }

    /**
     * Generate a new puzzle and reset the game board of cells based on the puzzle.
     * You can call this method to start a new game.
     */
    public void newGame(int difficulty) {
        puzzle.newPuzzle(difficulty);
        difficultyLabel.setText(currentDifficultyStr);
        
        // Initialize all the 9x9 cells, based on the puzzle.
        for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
                cells[row][col].setNumberAndStatus(0, false);
                cells[row][col].setNumberAndStatus(puzzle.numbers[row][col], puzzle.isGiven[row][col]);
                cells[row][col].getStyleClass().clear();
                cells[row][col].getStyleClass().add("text-field-style");
            }
        }

        // Reset the timer
        seconds = 0;
        timeLabel.setText("00:00:00");
        if (timeline != null) {
            timeline.stop();
        }
        startTimer();

        System.out.println("A new game has been created");
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

    private class CellInputListener implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            // Get a reference of the Cell that triggers this action event
            Cell sourceCell = (Cell) event.getSource();

            selectedCell = sourceCell;
    
            // Retrieve the text entered in the cell
            String text = sourceCell.getText();
    
            // Try parsing the text to an integer
            try {
                int numberIn = Integer.parseInt(text);
                // Rest of your code handling the parsed integer
            
                /*
                * Check the numberIn against sourceCell.number.
                * Update the cell status sourceCell.status,
                * and re-paint the cell via sourceCell.paint().
                */
                if (numberIn == sourceCell.getNumber()) {
                    sourceCell.setStatus(CellStatus.CORRECT_GUESS);
                    sourceCell.getStyleClass().addAll("text-field-style", "correct-guess");
                    sourceCell.setEditable(false);
                    System.out.println("Correct guess");
                } else {
                    sourceCell.setStatus(CellStatus.WRONG_GUESS);
                    sourceCell.getStyleClass().addAll("text-field-style", "wrong-guess");
                    System.out.println("Wrong guess");
                }
                
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
                System.err.println("Invalid input: " + text);
            }
        }
    }

    private void handleNumberButtonClick(int number) {
        if (selectedCell != null && selectedCell.isEditable()) {
            selectedCell.setText(String.valueOf(number));
            selectedCell.fireEvent(new ActionEvent());
        }
    }

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
        }
    }

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