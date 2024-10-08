package main;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

/**
 * The LayerOne represents the first layer of visual processing that the human brain does.
 * Layer zero is processing the actual visual into some sort of simplified input stream,
 * which in this case is done by creating a VisionBoard that converts the image to a 5x5 grid.
 * The LayerOne then calculates some estimated weight for each possible figure that a digit may have.
 * Layer Two/Three is assessing the weights of each figure and combining them to distinguish digits
 * which is done here in the VisionPanel
 */
public class LayerOne {

  // The 5x5 grid in use. This could have just been a fixed size array, but I wanted
  // to make this scalable in case I wanted to change the dimensions.
  private ArrayList<ArrayList<Double>> grid;

  /**
   * Constructs a LayerOne with the given grid.
   * @param grid the grid to be used
   */
  public LayerOne(ArrayList<ArrayList<Double>> grid) {
    this.grid = grid;
  }

  /**
   * Draws the grid stored in this LayerOne.
   * @param g the Graphics object used to draw
   */
  public void drawComponent(Graphics g) {
    
    // In effect, we draw a simplified version of the relevant data from the user's drawing.
    int squareSize = PaintWindow.WIDTH / PaintPanel.SCALE;
    for (int col = 0; col < this.grid.size(); col += 1) {
      for (int row = 0; row < this.grid.size(); row += 1) {
        int current = (int) (this.grid.get(col).get(row) * 255);
        g.setColor(new Color(0, 0, 0, current));
        g.fillRect(col * squareSize, row * squareSize, squareSize, squareSize);
      }
    }
  }

  /*
   * Below are all methods used to assess the likelihood that a given figure may be in the digit.
   * Weight values are arbitrary and were adjusted as I saw fit in order to make the program work.
   * This could have been done using some sort of ML algorithm like linear perceptron, but I wanted to
   * experiment myself and see how close I could get it without offloading the work.
   * 
   * Each method is given an arbitrary letter to symbolize the figure associated with it. These were chosen
   * simply to facilitate adjustments and Layer 2-3 processing when writing code. Each method has above it in
   * a comment a note of what it actually represents.
   * 
   * See the attached image in the README for a breakdown of each figure and which numbers they are commonly found in
   * 
   * They commonly work as follows:
   * 1. If the cells that are most frequently a part of that figure are completely deactivated, then that
   *    figure must not be present, return 0.
   * 2. Then, if the cells on the edge of that figure are completely empty, then that figure is likely somewhat present,
   *    but we lower the scaling of the weights so the confidence is lower.
   * 3. Otherwise, the figure is likely quite prominent, so we return a fairly highly weighted value for it.
   */
  
  // BOTTOM HORIZONTAL
  public double hasFigureA() {

    if (this.grid.get(1).get(4) == 0 || this.grid.get(3).get(4) == 0) {
      return 0.0;
    }

    if (this.grid.get(0).get(4) == 0 || this.grid.get(4).get(4) == 0) {
      return this.grid.get(1).get(4) * 0.5 + this.grid.get(2).get(4) * 0.8
          + this.grid.get(3).get(4) * 0.5;
    }

    if (this.grid.get(0).get(4) < 0.4 || this.grid.get(4).get(4) < 0.4) {
      return this.grid.get(1).get(4) + this.grid.get(2).get(4) * 1.1 + this.grid.get(3).get(4)
          + (this.grid.get(0).get(4) + this.grid.get(4).get(4)) / 5.0;
    }

    return this.grid.get(1).get(4) * 1.1 + this.grid.get(2).get(4) * 1.3
        + this.grid.get(3).get(4) * 1.1 + (this.grid.get(0).get(4) + this.grid.get(4).get(4)) / 5.0;
  }

  // TOP HORIZONTAL
  public double hasFigureB() {

    if (this.grid.get(1).get(0) == 0 || this.grid.get(2).get(0) == 0 || this.grid.get(3).get(0) == 0) {
      return 0.0;
    }

    if (this.grid.get(0).get(0) == 0 || this.grid.get(4).get(0) == 0) {
      return this.grid.get(1).get(0) * 0.5 + this.grid.get(2).get(0) * 0.8
          + this.grid.get(3).get(0) * 0.5;
    }

    if (this.grid.get(0).get(0) < 0.4 || this.grid.get(4).get(0) < 0.4) {
      return this.grid.get(1).get(0) + this.grid.get(2).get(0) * 1.1 + this.grid.get(3).get(0)
          + (this.grid.get(0).get(0) + this.grid.get(4).get(0)) / 5.0;
    }

    return this.grid.get(1).get(0) * 1.1 + this.grid.get(2).get(0) * 1.3
        + this.grid.get(3).get(0) * 1.1 + (this.grid.get(0).get(0) + this.grid.get(4).get(0)) / 5.0;
  }

  // RIGHT VERTICAL
  public double hasFigureC() {

    if (this.grid.get(3).get(1) == 0 || this.grid.get(3).get(3) == 0) {
      return 0.0;
    }

    if (this.grid.get(3).get(0) == 0 || this.grid.get(3).get(4) == 0) {
      return this.grid.get(3).get(1) * 0.5 + this.grid.get(3).get(2) * 0.8
          + this.grid.get(3).get(3) * 0.5;
    }

    if (this.grid.get(3).get(0) < 0.4 || this.grid.get(3).get(4) < 0.4) {
      return this.grid.get(3).get(1) + this.grid.get(3).get(2) * 1.1 + this.grid.get(3).get(3)
          + (this.grid.get(3).get(0) + this.grid.get(3).get(4)) / 5.0;
    }

    return this.grid.get(3).get(1) * 1.1 + this.grid.get(3).get(2) * 1.3
        + this.grid.get(3).get(3) * 1.1 + (this.grid.get(3).get(0) + this.grid.get(4).get(4)) / 5.0;
  }

  // CENTER VERTICAL
  public double hasFigureD() {

    if (this.grid.get(2).get(1) == 0 || this.grid.get(2).get(3) == 0) {
      return 0.0;
    }

    if (this.grid.get(2).get(0) == 0 || this.grid.get(2).get(4) == 0) {
      return this.grid.get(2).get(1) * 0.5 + this.grid.get(2).get(2) * 0.8
          + this.grid.get(2).get(3) * 0.5;
    }

    if (this.grid.get(2).get(0) < 0.4 || this.grid.get(2).get(4) < 0.4) {
      return this.grid.get(2).get(1) + this.grid.get(2).get(2) * 1.1 + this.grid.get(2).get(3)
          + (this.grid.get(2).get(0) + this.grid.get(2).get(4)) / 5.0;
    }

    return this.grid.get(2).get(1) * 1.1 + this.grid.get(2).get(2) * 1.3
        + this.grid.get(2).get(3) * 1.1 + (this.grid.get(2).get(0) + this.grid.get(2).get(4)) / 5.0;
  }

  // DIAGONAL (2,4) to (3, 0) or (1,4) to (3,0)
  public double hasFigureE() {

    if ((this.grid.get(2).get(3) <= 0.2 && this.grid.get(3).get(1) <= 0.2)
        || (this.grid.get(1).get(3) <= 0.2 && this.grid.get(3).get(1) <= 0.2)) {
      return 0.0;
    }

    if (this.grid.get(2).get(4) == 0 || this.grid.get(3).get(0) == 0) {
      return Math.max(this.grid.get(2).get(3) * 0.5 + this.grid.get(2).get(2) * 0.8
          + this.grid.get(3).get(1) * 0.5, this.grid.get(1).get(3) * 0.5 + this.grid.get(2).get(2) * 0.8
          + this.grid.get(3).get(1) * 0.5);
    }

    if (this.grid.get(0).get(4) < 0.4 || this.grid.get(4).get(0) < 0.4) {
      return Math.max(this.grid.get(2).get(3) + this.grid.get(2).get(2) * 1.1 + this.grid.get(3).get(1)
          + (this.grid.get(2).get(4) + this.grid.get(3).get(0)) / 5.0,
          this.grid.get(1).get(3) + this.grid.get(2).get(2) * 1.1 + this.grid.get(3).get(1)
          + (this.grid.get(1).get(4) + this.grid.get(3).get(0)) / 5.0);
    }

    return Math.max(this.grid.get(2).get(3) * 1.1 + this.grid.get(2).get(2) * 1.3
        + this.grid.get(3).get(1) * 1.1 + (this.grid.get(2).get(4) + this.grid.get(3).get(0)) / 5.0,
        this.grid.get(1).get(3) * 1.1 + this.grid.get(2).get(2) * 1.3
        + this.grid.get(3).get(1) * 1.1 + (this.grid.get(1).get(4) + this.grid.get(3).get(0)) / 5.0);
  }

  // CENTER HORIZONTAL
  public double hasFigureF() {
    
    if (this.grid.get(1).get(2) == 0 || this.grid.get(3).get(2) == 0) {
      return 0.0;
    }

    if (this.grid.get(0).get(2) == 0 || this.grid.get(4).get(2) == 0) {
      return this.grid.get(1).get(2) * 0.75 + this.grid.get(2).get(2) * 1
          + this.grid.get(3).get(2) * 0.75;
    }

    if (this.grid.get(0).get(2) < 0.4 || this.grid.get(4).get(2) < 0.4) {
      return this.grid.get(1).get(2) + this.grid.get(2).get(2) * 1.1 + this.grid.get(3).get(2)
          + (this.grid.get(0).get(2) + this.grid.get(4).get(2)) / 5.0;
    }

    return this.grid.get(1).get(2) * 1.1 + this.grid.get(2).get(2) * 1.3
        + this.grid.get(3).get(2) * 1.1 + (this.grid.get(0).get(2) + this.grid.get(4).get(2)) / 5.0;
  }

  // LEFT FORK
  public double hasFigureG() {

    if (this.grid.get(1).get(0) == 0 || this.grid.get(1).get(1) == 0) {
      return 0.0;
    }

    return this.grid.get(1).get(0) * 0.5 + this.grid.get(1).get(1) * 0.8
        + this.grid.get(1).get(2) * 0.5;

  }

  // LEFT LEG
  public double hasFigureH() {

    if (this.grid.get(1).get(3) == 0 || this.grid.get(1).get(4) == 0) {
      return 0.0;
    }

    return Math.max(this.grid.get(1).get(2) * 0.5 + this.grid.get(1).get(3) * 0.8
        + this.grid.get(1).get(4) * 0.5, this.grid.get(0).get(2) * 0.5 + this.grid.get(0).get(3) * 0.8
        + this.grid.get(0).get(4) * 0.5);

  }

  // TOPLEFT ROOF
  public double hasFigureI() {

    if (this.grid.get(0).get(0) == 0 || this.grid.get(1).get(0) == 0
        || this.grid.get(2).get(0) == 0) {
      return 0.0;
    }

    return this.grid.get(0).get(0) * 0.5 + this.grid.get(1).get(0) * 0.8
        + this.grid.get(2).get(0) * 0.5;

  }

  // LEFT CURVE
  public double hasFigureJ() {

    if (this.grid.get(1).get(1) == 0 || this.grid.get(1).get(3) == 0) {
      return 0.0;
    }

    if (this.grid.get(2).get(0) == 0 || this.grid.get(2).get(4) == 0) {
      return this.grid.get(1).get(1) * 0.5 + this.grid.get(1).get(2) * 0.8
          + this.grid.get(1).get(3) * 0.5;
    }

    if (this.grid.get(2).get(0) < 0.4 || this.grid.get(2).get(4) < 0.4) {
      return this.grid.get(1).get(1) + this.grid.get(1).get(2) * 1.1 + this.grid.get(1).get(3)
          + (this.grid.get(2).get(0) + this.grid.get(2).get(4)) / 5.0;
    }

    return this.grid.get(1).get(1) * 1.1 + this.grid.get(1).get(2) * 1.3
        + this.grid.get(1).get(3) * 1.1 + (this.grid.get(2).get(0) + this.grid.get(2).get(4)) / 5.0;
  }

  // CENTER RIGHT CURVE
  public double hasFigureK() {

    if (this.grid.get(2).get(1) == 0 || this.grid.get(2).get(3) == 0) {
      return 0.0;
    }

    if (this.grid.get(3).get(0) == 0 || this.grid.get(3).get(4) == 0) {
      return this.grid.get(2).get(1) * 0.5 + this.grid.get(2).get(2) * 0.8
          + this.grid.get(2).get(3) * 0.5;
    }

    if (this.grid.get(3).get(0) < 0.4 || this.grid.get(3).get(4) < 0.4) {
      return this.grid.get(2).get(1) + this.grid.get(2).get(2) * 1.1 + this.grid.get(2).get(3)
          + (this.grid.get(3).get(0) + this.grid.get(3).get(4)) / 5.0;
    }

    return this.grid.get(2).get(1) * 1.1 + this.grid.get(2).get(2) * 1.3
        + this.grid.get(2).get(3) * 1.1 + (this.grid.get(3).get(0) + this.grid.get(2).get(4)) / 5.0;
  }
  
  // TOPCENTER ROOF
  public double hasFigureL() {
    
    if (this.grid.get(2).get(0) <= .2) {
      return 0.0;
    }

    if (this.grid.get(1).get(0) == 0 || this.grid.get(2).get(0) == 0
        || this.grid.get(3).get(0) == 0) {
      return 0.0;
    }

    return this.grid.get(1).get(0) * 0.3 + this.grid.get(2).get(0) * 0.5
        + this.grid.get(3).get(0) * 0.3;

  }
  
  // RIGHT LEG
  public double hasFigureM() {
    
    if (this.grid.get(3).get(3) == 0 || this.grid.get(3).get(4) == 0) {
      return 0.0;
    }

    return Math.max(this.grid.get(3).get(2) * 0.5 + this.grid.get(3).get(3) * 0.8
        + this.grid.get(3).get(4) * 0.5, this.grid.get(4).get(2) * 0.5 + this.grid.get(4).get(3) * 0.8
        + this.grid.get(4).get(4) * 0.5);
    
  }
  
  // DIAGONAL (1,0) to (3, 4)
  public double hasFigureN() {

    if (this.grid.get(1).get(1) <= 0.2 || this.grid.get(2).get(2) < .4 || this.grid.get(3).get(3) <= 0.2) {
      return 0.0;
    }

    if (this.grid.get(1).get(0) == 0 || this.grid.get(3).get(4) == 0) {
      return this.grid.get(1).get(1) * 0.5 + this.grid.get(2).get(2) * 0.8
          + this.grid.get(3).get(3) * 0.5;
    }

    if (this.grid.get(1).get(0) < 0.4 || this.grid.get(3).get(4) < 0.4) {
      return this.grid.get(1).get(1) + this.grid.get(2).get(2) * 1.1 + this.grid.get(3).get(3)
          + (this.grid.get(1).get(0) + this.grid.get(3).get(4)) / 5.0;
    }

    return this.grid.get(1).get(1) * 1.1 + this.grid.get(2).get(2) * 1.3
        + this.grid.get(3).get(3) * 1.1 + (this.grid.get(1).get(0) + this.grid.get(3).get(4)) / 5.0;
  }
  
  // MID-HIGH CENTER ROOF
  public double hasFigureO() {
    
    if (this.grid.get(2).get(1) <= .2) {
      return 0.0;
    }

    if (this.grid.get(1).get(1) == 0 || this.grid.get(2).get(1) == 0
        || this.grid.get(3).get(1) == 0) {
      return 0.0;
    }

    return this.grid.get(1).get(1) * 0.3 + this.grid.get(2).get(1) * 0.5
        + this.grid.get(3).get(1) * 0.3;
  }
  
  // RIGHT FORK
  public double hasFigureP() {

    if (this.grid.get(3).get(0) == 0 || this.grid.get(3).get(1) == 0) {
      return 0.0;
    }

    return this.grid.get(3).get(0) * 0.5 + this.grid.get(3).get(1) * 0.8
        + this.grid.get(3).get(2) * 0.5;

  }
  
  // TOPLEFT CURVE (on a 2)
  public double hasFigureQ() {

    return this.grid.get(0).get(1) * 0.5 + this.grid.get(0).get(0) * 0.8
        + this.grid.get(1).get(0) * 0.5;

  }
  
  // FAR LEFT VERTICAL (only used in detecting zeroes)
  public double hasFigureR() {

    if (this.grid.get(0).get(1) == 0 || this.grid.get(0).get(3) == 0) {
      return 0.0;
    }

    if (this.grid.get(0).get(0) == 0 || this.grid.get(0).get(4) == 0) {
      return this.grid.get(0).get(1) * 0.5 + this.grid.get(0).get(2) * 0.8
          + this.grid.get(0).get(3) * 0.5;
    }

    if (this.grid.get(0).get(0) < 0.4 || this.grid.get(0).get(4) < 0.4) {
      return this.grid.get(0).get(1) + this.grid.get(0).get(2) * 1.1 + this.grid.get(0).get(3)
          + (this.grid.get(0).get(0) + this.grid.get(0).get(4)) / 5.0;
    }

    return this.grid.get(0).get(1) * 1.1 + this.grid.get(0).get(2) * 1.3
        + this.grid.get(0).get(3) * 1.1 + (this.grid.get(0).get(0) + this.grid.get(0).get(4)) / 5.0;
  }
  
  // FAR RIGHT VERTICAL (only used in detecting zeroes)
  public double hasFigureS() {

    if (this.grid.get(4).get(1) == 0 || this.grid.get(4).get(3) == 0) {
      return 0.0;
    }

    if (this.grid.get(4).get(0) == 0 || this.grid.get(4).get(4) == 0) {
      return this.grid.get(4).get(1) * 0.5 + this.grid.get(4).get(2) * 0.8
          + this.grid.get(4).get(3) * 0.5;
    }

    if (this.grid.get(4).get(0) < 0.4 || this.grid.get(4).get(4) < 0.4) {
      return this.grid.get(4).get(1) + this.grid.get(4).get(2) * 1.1 + this.grid.get(0).get(3)
          + (this.grid.get(4).get(0) + this.grid.get(4).get(4)) / 5.0;
    }

    return this.grid.get(4).get(1) * 1.1 + this.grid.get(4).get(2) * 1.3
        + this.grid.get(4).get(3) * 1.1 + (this.grid.get(4).get(0) + this.grid.get(0).get(4)) / 5.0;
  }
  
  
  

}
