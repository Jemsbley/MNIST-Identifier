package main;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

/**
 * Stores and manipulates the perceived data from the base image. Effectively layer zero of processing.
 */
public class VisionBoard implements OptionalVisionBoard {

  private ArrayList<ArrayList<Boolean>> oldBoard; // The large image that is taken in (the distal stimulus)
  private ArrayList<ArrayList<Boolean>> board; // The relevant part of that image (the smallest square region that contains all active pixels)

  /**
   * Creates a VisionBoard object.
   * @param oldBoard the distal stimulus board taken in
   */
  public VisionBoard(ArrayList<ArrayList<Boolean>> oldBoard) {

    this.oldBoard = oldBoard;

    // Find the last activated pixel at each extremity.
    int left = this.findEdge("left");
    int right = this.findEdge("right");
    int top = this.findEdge("top");
    int bottom = this.findEdge("bottom");

    int width = (right - left) + 1;
    int height = (bottom - top) + 1;

    // Here we make the bounds square, and prevent them from going out of bounds of the image
    if (height > width) {
      left -= (height - width) / 2;
      right += (height - width) / 2 + (height - width) % 2;
      width = right - left + 1;
    }
    else if (width > height) {
      top -= (width - height) / 2;
      bottom += (width - height) / 2 + (width - height) % 2;
      height = bottom - top + 1;
    }

    if (bottom >= PaintPanel.SCALE) {
      top -= bottom - PaintPanel.SCALE;
      bottom = PaintPanel.SCALE - 1;
    }
    else if (top < 0) {
      bottom -= top;
      top = 0;
    }

    if (right >= PaintPanel.SCALE) {
      left -= right - PaintPanel.SCALE;
      right = PaintPanel.SCALE - 1;
    }
    else if (left < 0) {
      right -= left;
      left = 0;
    }

    // Create a new square matrix to hold only the relevant information
    ArrayList<ArrayList<Boolean>> newBoard = new ArrayList<ArrayList<Boolean>>();

    for (int col = 0; col <= width; col += 1) {
      ArrayList<Boolean> nextCol = new ArrayList<>();
      for (int row = 0; row <= height; row += 1) {
        nextCol.add(this.oldBoard.get(left + col).get(top + row));
      }
      newBoard.add(nextCol);
    }

    this.board = newBoard;

  }

  /**
   * Generates a LayerOne object with a 5x5 grid (the proximal stimulus).
   * @return
   */
  public LayerOne generateGrid() {
    int step = this.board.size() / 5; // Allows us to iterate through the board one fifth at a time

    ArrayList<ArrayList<Double>> l1 = new ArrayList<>(); // The 5x5 matrix to be used

    // This loop generates relevant information about the square board and simplifies it into a 5x5 board by summarizing/averaging sections 
    int remCount = 0;
    for (int col = 0; col < this.board.size(); col += step) {
      ArrayList<Double> nextCol = new ArrayList<Double>();
      int rem = this.board.size() % 5;
      col -= remCount;
      remCount = 0;
      for (int row = 0; row < this.board.size(); row += step) {
        if (rem > 0) {
          nextCol.add(this.averageSection(col, row, step + 1));
          rem -= 1;
          col += 1;
          row += 1;
          remCount += 1;
        }
        else {
          nextCol.add(this.averageSection(col, row, step));
        }
      }
      l1.add(nextCol);
    }
    return new LayerOne(l1);
  }

  /**
   * Counts the activate neighbors of a given cell. This is relevant information when simplifying the image,
   * so cells can consider the activation of those that surround them, rather than solely the space they occupy.
   * Indirectly, this allows lines to become more cohesive, since a line would typically span more than one fifth
   * of an image, so we can raise the activation of both cells to make it more clear that a line is present.
   * @param col the column to check
   * @param row the row to check
   * @return the number of active neighbors, adjacent and diagonal
   */
  private int countActiveNeighbors(int col, int row) {
    int count = 0;
    if (col != 0 && this.board.get(col - 1).get(row)) {
      count += 1;
    }
    if (col != this.board.size() - 1 && this.board.get(col + 1).get(row)) {
      count += 1;
    }
    if (row != 0 && this.board.get(col).get(row - 1)) {
      count += 1;
    }
    if (row != this.board.size() - 1 && this.board.get(col).get(row + 1)) {
      count += 1;
    }
    
    if (col != 0 && row != 0 && this.board.get(col - 1).get(row-1)) {
      count += 1;
    }
    if (col != 0 && row != this.board.size() - 1 && this.board.get(col - 1).get(row+1)) {
      count += 1;
    }
    if (col != this.board.size() - 1 && row != 0 && this.board.get(col + 1).get(row-1)) {
      count += 1;
    }
    if (col != this.board.size() - 1 && row != this.board.size() - 1 && this.board.get(col + 1).get(row+1)) {
      count += 1;
    }
    return count;
  }

  /**
   * Calculates the weighted average value of a specified square of cells.
   * @param left the left bound (inclusive)
   * @param top the top bound (inclusive)
   * @param dim the amount of cells in both directions to consider (going from left to left + dim - 1 and similarly for top)
   * @return the weighted average value across that region
   * Note that the weights here are completely arbitrary and were created and adjusted manually to get the program to work.
   * This would be notably more effective using an ML algorithm, but I wanted to try doing it by hand.
   */
  private double averageSection(int left, int top, int dim) {

    double total = 0.0;

    for (int col = left; col < left + dim; col += 1) {
      for (int row = top; row < top + dim; row += 1) {
        if (this.board.get(col).get(row)) {
          int neighbors = this.countActiveNeighbors(col, row);
          if (neighbors == 0) {
            total += .05;
          }
          else if (neighbors == 1) {
            total += .3;
          }
          else if (neighbors == 2) {
            total += .7;
          }
          else if (neighbors == 3) {
            total += .8;
          }
          else if (neighbors == 4) {
            total += .9;
          }
          else if (neighbors == 5) {
            total += 1;
          }
          else if (neighbors == 6) {
            total += 1.1;
          }
          else if (neighbors == 7) {
            total += 1.15;
          }
          else if (neighbors == 8) {
            total += 1.3;
          }
        }
      }
    }

    return total / (dim * dim * 1.44);

  }

  /**
   * Finds the last active pixel in a certain direction of the board.
   * @param edge the edge to find the location of
   * @return the index at which that edge can be found
   */
  public int findEdge(String edge) {
    if (edge.equals("left")) {
      return findSideBound(0, 0, this.oldBoard.size(), 1);
    }
    else if (edge.equals("right")) {
      return findSideBound(this.oldBoard.size() - 1, 0, this.oldBoard.size(), -1);
    }
    else if (edge.equals("top")) {
      return findCFBound(0, 0, this.oldBoard.size(), 1);
    }
    else if (edge.equals("bottom")) {
      return findCFBound(this.oldBoard.size() - 1, 0, this.oldBoard.size(), -1);
    }
    else {
      throw new IllegalArgumentException("Invalid edge name");
    }
  }

  /**
   * Finds the side bound of the board given where to start, stop, and how to move.
   * @param start the first index to check
   * @param lower the leftmost index that may be searched
   * @param upper the rightmost index that may be searched
   * @param stepDirection either -1 or 1, the direction in which the checked position will change
   * @return the index at which the bound is found.
   */
  private int findSideBound(int start, int lower, int upper, int stepDirection) {
    int col = start;
    while (col >= lower && col < upper) {
      int row = start;
      while (row >= lower && row < upper) {
        if (this.oldBoard.get(col).get(row)) {
          return col;
        }
        row += 1 * stepDirection;
      }
      col += 1 * stepDirection;
    }
    throw new IllegalArgumentException("There is no drawing");
  }

  /**
   * Finds the ceiling or the floor bound of the board given where to start, stop, and how to move.
   * @param start the first index to check
   * @param lower the uppermost index that may be searched
   * @param upper the lowermost index that may be searched
   * @param stepDirection either -1 or 1, the direction in which the checked position will change
   * @return the index at which the bound is found.
   */
  private int findCFBound(int start, int lower, int upper, int stepDirection) {

    int row = start;
    while (row >= lower && row < upper) {
      int col = start;
      while (col >= lower && col < upper) {
        if (this.oldBoard.get(col).get(row)) {
          return row;
        }
        col += 1 * stepDirection;
      }
      row += 1 * stepDirection;
    }
    throw new IllegalArgumentException("There is no drawing");
  }

  /**
   * Draws this vision board using the given Graphics object.
   * This renders the 5x5 drawing seen in the small window
   * @param g the Graphics object used to draw.
   */
  public void drawComponent(Graphics g) {
    int squareSize = PaintWindow.WIDTH / PaintPanel.SCALE;
    for (int col = 0; col < this.board.size(); col += 1) {

      for (int row = 0; row < this.board.size(); row += 1) {
        if (this.board.get(col).get(row)) {
          g.setColor(Color.BLACK);
        }
        else {
          g.setColor(Color.WHITE);
        }
        g.fillRect(col * squareSize, row * squareSize, squareSize, squareSize);
      }

    }
  }

  /**
   * A VisionBoard is a VisionBoard, so it may be abstracted.
   * @return true
   */
  public boolean isVisionBoard() {
    return true;
  }

  /**
   * A VisionBoard is a VisionBoard, so it will be abstracted.
   * @return this VisionBoard
   */
  public VisionBoard extract() {
    return this;
  }

}
