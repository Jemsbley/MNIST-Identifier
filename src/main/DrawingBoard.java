package main;

import java.util.ArrayList;

/**
 * DrawingBoard is the object that drawing is actually done on.
 */
public class DrawingBoard {

  ArrayList<ArrayList<Boolean>> board; // Indices are (col, row) from top left (0,0) to bottom right

  public DrawingBoard() {

    // Create a 2D matrix with all false values to represent each cell
    ArrayList<ArrayList<Boolean>> fullBoard = new ArrayList<>();

    for (int col = 0; col < PaintPanel.SCALE; col += 1) {
      ArrayList<Boolean> nextRow = new ArrayList<>();
      for (int row = 0; row < PaintPanel.SCALE; row += 1) {
        nextRow.add(false);
      }
      fullBoard.add(nextRow);
    }

    this.board = fullBoard;
  }

  /**
   * Return the value at the given indices.
   * @param col the column to check
   * @param row the row to check
   * @return the boolean value stored in the grid
   */
  public boolean getVal(int col, int row) {
    return this.board.get(col).get(row);
  }

  /**
   * Draws a brush stroke at the given coordinates with a stroke width of 2.
   * @param col the column to draw at
   * @param row the row to draw at
   * @param val the value (draw/erase) to use
   */
  public void setVal(int col, int row, boolean val) {
    this.board.get(col).set(row, val);
    this.board.get(boundOut(col + 1)).set(row, val);
    this.board.get(boundOut(col - 1)).set(row, val);
    this.board.get(col).set(boundOut(row + 1), val);
    this.board.get(col).set(boundOut(row - 1), val);
  }

  /**
   * Limits an integer to fit the bounds of the grid. This is useful when drawing, so inadvertent exceptions
   * regarding indexing don't occur.
   * @param val the value to be bounded
   * @return the bounded form of that value
   */
  private int boundOut(int val) {
    if (val < 0) {
      return 0;
    }
    else if (val > PaintPanel.SCALE - 1) {
      return PaintPanel.SCALE - 1; // Since our grid is square, we don't need to compare width or height, the scale is enough
    }
    return val;
  }
  
  /**
   * Determines if this grid is completely empty (erased).
   * @return whether the grid is empty
   */
  public boolean isEmpty() {
    for (int col = 0; col < PaintPanel.SCALE; col += 1) {
      for (int row = 0; row < PaintPanel.SCALE; row += 1) {
        if (this.board.get(col).get(row)) { // If any one value in the grid is not false, then the grid is not empty
          return false;
        }
      }
    }
    return true;
  }
  
  /**
   * Creates a new VisionBoard given this board as a percept.
   * @return the VisionBoard that holds this DrawingBoard.
   */
  public VisionBoard makeVisionBoard() {
    return new VisionBoard(this.board);
  }

}
