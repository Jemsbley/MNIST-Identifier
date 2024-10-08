package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

/**
 * This is the object that the user actually draws on. It takes in mouse and keyboard input and alters the drawing.
 */
public class PaintPanel extends JPanel {

  // Mouse and key listeners
  private final MListener mlistener;
  private final KListener klistener;

  // Scale for each "pixel" on the drawing
  public static final int SCALE = 50;

  private DrawingBoard board; // Where the drawing is stored/manipulated
  private OptionalVisionBoard vb; // An optional VisionBoard, which stores the data actually used to compute shapes
  private final VisionWindow vw; // The window for the VisionBoard
  
  // Whether or not the mouse is currently being dragged
  private boolean dragValue;

  /**
   * Instantiates the PaintPanel with expected default values.
   */
  public PaintPanel() {
    this.mlistener = new MListener();
    this.addMouseListener(mlistener); // JPanel implementation of mouse and key listeners
    this.addMouseMotionListener(mlistener);
    this.klistener = new KListener();
    this.addKeyListener(klistener);
    
    this.dragValue = true; // Drag value begins as true so if the user's first click is a drag it is interpreted correctly
    
    this.vb = new EmptyVisionBoard(); // Since nothing is drawn, there is nothing for the program to analyze, we create an empty vision board
    this.vw = new VisionWindow(vb); // Create a VisionWindow but don't show it

    this.setFocusable(true); // Ensures that you can focus this window

    this.board = new DrawingBoard(); // Creates a new DrawingBoard to enable drawing

    this.repaint(); // Draws this component
  }

  /**
   * This is called every tick by repaint, it creates a graphic shown on screen.
   * @param g the Graphics object used to draw
   */
  public void paintComponent(Graphics g) {

    // Ensures that all "pixel"s will fit on screen
    int squareSize = PaintWindow.WIDTH / PaintPanel.SCALE;

    // This will iterate through the DrawingBoard and turn black any pixel that is activated and white any pixel that isn't
    for (int col = 0; col < PaintPanel.SCALE; col += 1) {
      for (int row = 0; row < PaintPanel.SCALE; row += 1) {
        if (this.board.getVal(col, row)) {
          g.setColor(Color.BLACK);
        }
        else {
          g.setColor(Color.WHITE);
        }
        g.fillRect(col * squareSize, row * squareSize, squareSize, squareSize);
      }

    }

    // If the OptionalVisionBoard stored has meaningful data, we will draw that as well
    if (this.vb.isVisionBoard()) {
      VisionBoard vsBoard = this.vb.extract(); // Extract the VisionBoard
      
      // Find all of the edges of the board (we want to include as little whitespace in our board as possible to minimize bad data)
      int left = vsBoard.findEdge("left");
      int right = vsBoard.findEdge("right");
      int top = vsBoard.findEdge("top");
      int bottom = vsBoard.findEdge("bottom");

      // Find the dimensions of the space found
      int width = (right - left) + 1;
      int height = (bottom - top) + 1;

      // Here we must make our dimensions square. This ensures that further calculations accurately assess equal amounts of the board
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

      // This makes sure that the square that will encapsulate the drawing does not go off screen
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

      // Find the coordinates to draw an encapsulating rectangle around the drawing
      int leftCoord = left * squareSize;
      g.setColor(Color.DARK_GRAY);
      
      // Draw an encapsulating rectangle around the entire drawing with 25 equal cells to show how the drawing will be simplified for computation
      for (int col = 0; col < 5; col += 1) {
        int topCoord = top * squareSize;
        for (int row = 0; row < 5; row += 1) {
          g.drawRect(leftCoord, topCoord, width * squareSize / 5, height * squareSize / 5);
          topCoord += height * squareSize / 5;
        }
        leftCoord += width * squareSize / 5;
      }

    }

  }

  /**
   * Updates the board with the given mouse event and whether the user is drawing or erasing.
   * @param e the mouse event
   * @param val the value to set the cell currently being pressed
   * The reason val is needed is to ensure that dragging does the correct operation for each cell.
   * Swing forces any drag operation to be BUTTON1, which is not always the case
   */
  public void updateBoard(MouseEvent e, boolean val) {
    
    // Find the coordinates of the mouse press
    int x = e.getX();
    int y = e.getY();

    // Set the board at the correct corresponding coordinates to the given value
    board.setVal(x / (PaintWindow.WIDTH / PaintPanel.SCALE),
        y / (PaintWindow.WIDTH / PaintPanel.SCALE), val);
    
    // Redraw so the user can see their action displayed
    repaint();
    
    // If the board has nothing we must clear the VisionBoard, otherwise update it with a current VisionBoard
    if (board.isEmpty()) {
      vb = new EmptyVisionBoard();
    }
    else {
      vb = board.makeVisionBoard();
    }
  }

  /**
   * KeyListener class to accurately evaluate key inputs.
   * Note that since only the two keys are relevant when typed,
   * the keyPressed and keyReleased methods are useless and therefore empty.
   */
  private class KListener implements KeyListener {

    /**
     * Correctly manipulates the state of the application based on key input
     * @param e the key pressed
     * 
     * Backspace clears the board and return predicts the digit based on what is currently on screen
     */
    public void keyTyped(KeyEvent e) {
      if (e.getKeyChar() == '\b') {
        vb = new EmptyVisionBoard();
        board = new DrawingBoard();
        vw.updateBoard(vb);
        repaint();
      } else if (e.getKeyChar() == '\n') {
        vw.updateBoard(vb);
        repaint();
      }
    }

    public void keyPressed(KeyEvent e) {

    }

    public void keyReleased(KeyEvent e) {

    }

  }

  /**
   * Mouse listener to accurately draw. We must also implement the MouseMotionListener to allow drag clicking.
   * Note that since only dragging and pressing down the mouse are relevant,
   * the mouseReleased, mouseEntered, mouseExited, and mouseMoved methods are useless and therefore empty.
   */
  private class MListener implements MouseListener, MouseMotionListener {

    public void mouseClicked(MouseEvent e) {

    }

    /**
     * Updates the board correctly given a mouse input.
     * @param e the button pressed
     * Left click is draw, right click is erase
     */
    public void mousePressed(MouseEvent e) {
      if (e.getButton() == MouseEvent.BUTTON3) {
        updateBoard(e, false);
        dragValue = false;
      }
      else if (e.getButton() == MouseEvent.BUTTON1) {
        updateBoard(e, true);
        dragValue = true;
      }

    }

    public void mouseReleased(MouseEvent e) {

    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    /**
     * Allows the user to drag to draw/erase.
     * @param e the mouse button pressed
     */
    public void mouseDragged(MouseEvent e) {
      if (contains(e.getPoint())) {
        updateBoard(e, dragValue);
      }
    }

    public void mouseMoved(MouseEvent e) {

    }

  }

}
