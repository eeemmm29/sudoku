// package com.example;

// import javafx.scene.control.TextField;

// /**
//  * The Cell class models the cells of the Sudoku puzzle by storing row/column,
//  * puzzle number, and status.
//  */
// public class Cell extends TextField {
//     /** The row and column number [0-8] of this cell */
//     int row, col;
//     /** The puzzle number [1-9] for this cell */
//     int number;
//     /** The status of this cell defined in enum CellStatus */
//     CellStatus status;

//     /** Constructor */
//     public Cell(int row, int col) {
//         this.row = row;
//         this.col = col;
//     }

//     /** Reset this cell for a new game, given the puzzle number and isGiven */
//     public void newGame(int number, boolean isGiven) {
//         this.number = number;
//         status = isGiven ? CellStatus.GIVEN : CellStatus.TO_GUESS;
//     }
// }

package com.example;

import java.util.function.UnaryOperator;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

/**
 * The Cell class models the cells of the Sudoku puzzle by storing puzzle number and status.
 */
public class Cell extends TextField {
    /** The puzzle number [1-9] for this cell */
    private int number;
    /** The status of this cell defined in enum CellStatus */
    private CellStatus status;
    /** The row and column number [0-8] of this cell */
    private int row, col;

    /** Constructor */
    public Cell() {
        // Constructor chaining to TextField
        super();
        setTextFormatter(new TextFormatter<>(digitFilter));
    }

    UnaryOperator<TextFormatter.Change> digitFilter = change -> {
        String newText = change.getControlNewText();
        // Allow empty input to clear the text field
        if (newText.isEmpty()) {
            return change;
        }
        // Check if the new text is a valid integer between 1 and 9 and contains only one digit
        if (newText.matches("[1-9]")) {
            return change;
        }
        // Check if the new text is a multi-digit number and extract the last digit
        if (newText.length() > 1 && newText.matches("\\d+")) {
            change.setText(newText.substring(newText.length() - 1));
            change.setRange(0, change.getControlText().length());
            return change;
        }
        // Otherwise, reject the change
        return null;
    };

    /**
     * Set the puzzle number and status for this cell.
     * @param number The puzzle number [1-9]
     * @param isGiven Indicates if the number is given (true) or to be guessed (false)
     */
    public void setNumberAndStatus(int number, boolean isGiven) {
        this.number = number;
        this.status = isGiven ? CellStatus.GIVEN : CellStatus.TO_GUESS;
        setText(isGiven ? String.valueOf(number) : ""); // Set the text of the cell
        setEditable(!isGiven); // Set editable status based on whether it's a given number
    }

    /**
     * Get the puzzle number of this cell.
     * @return The puzzle number [1-9]
     */
    public int getNumber() {
        return number;
    }

    /**
     * Get the status of this cell.
     * @return The status of this cell
     */
    public CellStatus getStatus() {
        return status;
    }

    /**
     * Set the position (row and column) of this cell.
     * @param row The row number [0-8]
     * @param col The column number [0-8]
     */
    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public void setStatus(CellStatus status) {
        this.status = status;
    }

    /**
     * Get the row number of this cell.
     * @return The row number [0-8]
     */
    public int getRow() {
        return row;
    }

    /**
     * Get the column number of this cell.
     * @return The column number [0-8]
     */
    public int getCol() {
        return col;
    }
}
