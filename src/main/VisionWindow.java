package main;

import javax.swing.JFrame;

/**
 * Window class to hold and display the VisionPanel.
 */
public class VisionWindow {
  private JFrame window; // The window
  private VisionPanel visionPanel; // The panel holding the board
  
  /**
   * Creates a VisionWindow.
   * @param vs the OptionalVisionBoard to be used
   */
  public VisionWindow(OptionalVisionBoard vs) {
    this.window = new JFrame("Vision"); // Window title
    this.visionPanel = new VisionPanel(vs); // Creates a new panel
    this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Closes when topleft X is pressed
    this.window.setResizable(false); // Prevents resizing.
    this.window.setSize(100, 100); // This is the relevant window size
    this.window.add(visionPanel); // Add the panel to the window
    this.window.setVisible(true); // Display it
  }
  
  /**
   * Updates the VisionBoard stored by the VisionPanel.
   * @param vs the new VisionBoard to be used
   */
  public void updateBoard(OptionalVisionBoard vs) {
    this.visionPanel.updateBoard(vs);
  }
}
