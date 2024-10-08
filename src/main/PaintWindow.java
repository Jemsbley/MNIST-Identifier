package main;

import javax.swing.JFrame;

/**
 * This class holds the actual window that you draw on.
 */
public class PaintWindow {

  private static JFrame window; // The window that displays on the OS
  private static PaintPanel panel; // The panel displayed on top of the window
  public static final int WIDTH = 500; // Sizing for the window
  public static final int HEIGHT = 500;

  /**
   * Runs the application.
   * @param args SPVM main args
   */
  public static void main(String[] args) {

    PaintWindow.window = new JFrame("Drawing Tablet"); // Instantiate the window
    PaintWindow.window.setLocationRelativeTo(null); // Center it
    PaintWindow.panel = new PaintPanel(); // Create a new panel to be painted on

    PaintWindow.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Sets the window to close when the X is pressed
    
    PaintWindow.window.setSize(PaintWindow.WIDTH  + 14, PaintWindow.HEIGHT + 37); // Sets the window size
    // The hanging constants are used for convenience of drawing, since windows puts curved edges on the page
    
    PaintWindow.window.setResizable(false); // Makes the window not resizable
    PaintWindow.window.add(PaintWindow.panel); // Actually puts the painting panel onto the window
    PaintWindow.window.setVisible(true); // Activates the window
  }

}
