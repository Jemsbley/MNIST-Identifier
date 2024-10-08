package main;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;

/**
 * The window used to draw the VisionBoard.
 */
public class VisionPanel extends JPanel {

  private OptionalVisionBoard vs; // The current VisionBoard

  /**
   * Constructs a VisionPanel with the given OptionalVisionBoard.
   * @param vs
   */
  public VisionPanel(OptionalVisionBoard vs) {
    this.vs = vs;
    this.repaint(); // Makes sure that the panel is drawn when constructed
  }

  /**
   * Allows the application to update a board without having to construct a new object.
   * @param vs the new OptionalVisionBoard to be used
   */
  public void updateBoard(OptionalVisionBoard vs) {
    this.vs = vs;
    this.repaint();
  }

  /**
   * This is called every tick by repaint, it creates a graphic shown on screen.
   * @param g the Graphics object used to draw
   */
  public void paintComponent(Graphics g) {
    
    // Start by filling the window with white
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, PaintPanel.SCALE * 50, PaintPanel.SCALE * 50);
    
    // If the VisionBoard has any meaningful data, extract it and convert it to a 5x5 ArrayList
    if (vs.isVisionBoard()) {
      VisionBoard vBoard = vs.extract();
      LayerOne l1 = vBoard.generateGrid(); // Generate a new LayerOne
      l1.drawComponent(g); // Draws the LayerOne onto the window, allowing us to see the simplified
      // version of the drawing

      // Here we calculate the estimated weights of each figure that a given digit may have.
      // This weight system is completely arbitrary and was manually adjusted as I tested out
      // Various drawings
      double a = l1.hasFigureA();
      double b = l1.hasFigureB();
      double c = l1.hasFigureC();
      double d = l1.hasFigureD();
      double e = l1.hasFigureE();
      double f = l1.hasFigureF();
      double fg = l1.hasFigureG(); // Called this fg since alias g was taken by the Graphics object
      double h = l1.hasFigureH();
      double i = l1.hasFigureI();
      double j = l1.hasFigureJ();
      double k = l1.hasFigureK();
      double l = l1.hasFigureL();
      double m = l1.hasFigureM();
      double n = l1.hasFigureN();
      double o = l1.hasFigureO();
      double p = l1.hasFigureP();
      double q = l1.hasFigureQ();
      double r = l1.hasFigureR();
      double s = l1.hasFigureS();

      // Here we count how many figures are certainly not active
      // The ones that are commonly in an 8 have a higher weight when not present
      double countOff = 0.0;
      if (a == 0) {
        countOff += 1.2;
      }
      if (b == 0) {
        countOff += 1.2;
      }
      if (c == 0) {
        countOff += 1.2;
      }
      if (d == 0) {
        countOff += 1;
      }
      if (e == 0) {
        countOff += 1;
      }
      if (f == 0) {
        countOff += 1.2;
      }
      if (fg == 0) {
        countOff += 1.2;
      }
      if (h == 0) {
        countOff += 1.2;
      }
      if (i == 0) {
        countOff += 1.2;
      }
      if (j == 0) {
        countOff += 1;
      }
      if (k == 0) {
        countOff += 1;
      }
      if (l == 0) {
        countOff += 1.2;
      }
      if (m == 0) {
        countOff += 1.2;
      }
      if (n == 0) {
        countOff += 1.2;
      }
      if (o == 0) {
        countOff += 1.2;
      }
      if (p == 0) {
        countOff += 1;
      }
      if (q == 0) {
        countOff += .2;
      }

      // K is a central curve to the right, present in 2, 3, and 8. 
      // D is a central vertical line. Typically, a digit only has one of these,
      // and it's more frequently D, so we lower the weighting of K based on D
      k = Math.max(k / 2.0, k - d);
      
      // E represents a diagonal that can be present under a combination of two vertical lines (c and d)
      // so we lower the frequency of E when those two are also there
      e = Math.max(e / 2.0, e - (c + d) / 2.0);

      // Displaying the estimated weights of each figure
      System.out.println("----------");
      System.out.println("base hor a " + a);
      System.out.println("top hor b " + b);
      System.out.println("right ver c " + c);
      System.out.println("center ver d " + d);
      System.out.println("diagonal e " + e);
      System.out.println("center hor f " + f);
      System.out.println("left fork fg " + fg);
      System.out.println("left leg h " + h);
      System.out.println("topleft roof i " + i);
      System.out.println("left curve j " + j);
      System.out.println("center right curve k " + k);
      System.out.println("topcenter roof l " + l);
      System.out.println("right leg m " + m);
      System.out.println("left diagonal n " + n);
      System.out.println("mig-high center roof o " + o);
      System.out.println("right fork p " + p);
      System.out.println("top-left curve q " + q);
      System.out.println("far left vertical r " + r);
      System.out.println("far right vertical s " + s);
      System.out.println("----------");

      // Calculate the estimated weight for each digit based on which figures are commonly present in them.
      // For example: a common 6 usually has most of a base horizontal line (a), a top horizontal line (b),
      // a center horizontal line (f), and a left fork (fg). In addition to b, it usually has a topleft horizontal (i)
      // and/or a topcenter horizontal (l). A 6 also notably does NOT have a right vertical (c),
      // a topright-downleft diagonal (e), or a centered right curve (k). That leads us to the equation for is6.
      // Using the same reasoning, we can calculate a fairly accurate estimation system for each digit.
      double is1 = (a + Math.max(d,j)) / 1.3 - (.7 * (2 * f + e + 1.1 * k + q));
      double is2 = (((a * 1.8 + e + b + i) / 4.0) + ((a * 1.8 + k * 1.5 + b + i) / 4.0))
          / 2.0 + 0.35 * q + .5 * l - (.5 * (f + j + Math.max(0, .25 - q) + (Math.max(0, (1 - 1.3 * a)))));
      double is3 = ((l + i) * 1.5 + (Math.max(c, d) + f + 1.4 * k) / 4.0 + m) / 2.0
          - (.8 * (fg * 2 + h + j + Math.max(0, (.3 - (l + o + c))) + Math.max(0,  (1 - 2.2 * c))));
      double is4 = (Math.max(c, d) + Math.max(f, o) + fg + p) / 3.0 - (.7 * (j + h + l * 1.3 + Math.max(0, (1 - c)) + Math.max(0, (1 - 1.3 * fg))));
      double is5 = (((a + b + f + 1.5 * fg) / 6.0) + ((a + b + f + fg + i * 2 + m) / 8.0) + l * 3) / 2.0 + o * .6
          - (.7 * (Math.max(0, (.5 - 1.2 * a)) + Math.max(0, (1 - 1.4 * fg)) + Math.max(0, (1 - 2 * f)) + e + d + 1.6 * j + h * 1.3 + 0.5 * p));
      double is6 = (a + b + f + fg + h + j + m) / 5.8 - .7 * (0.7 * p + e + k + o + Math.max(0, (1 - 1.4 * Math.max(h, j))));
      double is7 = (b + Math.max(c, e) + 0.9 * k) / 3.0 - (.7 * (3 * a + fg * 2 + j + Math.max(0,  1 - 1.3 * b)));
      double is8 = (a + b + c + d + e + f * 2 + fg * 2 + h + i + j + k + l + 0.7 * n + 1.5 * o + p) / 16.5 - (0.7 * Math.max(0, (1 - 1.1 * (fg + h))));
      double is9 = (b + l + f + fg + Math.max(c, d)) / 4.5 + 0.6 * (o + p) -
          (.7 * (a + e + h + i + Math.max(0, (1 - 1.3 * b)) + Math.max(0, (.5 - 1.3 * o)) + Math.max(0, (1 - 2 * l))));
      double is0 = ((a + b + c + j) / 4.0 + (a + b + c + e + j) / 5.0) / 2.0 + (j + k + l + m) / 5.0 + 0.6 * (r + s)
          - .7 * (f * 1.3 + k + n + Math.max(0, (1 - j)) + Math.max(0, (1 - 1.7 * p)));
      
      // The weights can go negative if too few of the features for them are present, so we zero out any negative
      // weights to make the system easier to read and understand
      is1 = Math.max(is1, 0);
      is2 = Math.max(is2, 0);
      is3 = Math.max(is3, 0);
      is4 = Math.max(is4, 0);
      is5 = Math.max(is5, 0);
      is6 = Math.max(is6, 0);
      is7 = Math.max(is7, 0);
      is8 = Math.max(is8, 0);
      is9 = Math.max(is9, 0);
      is0 = Math.max(is0, 0);

      // Since 8 usually has most of the features than any other number would have, it's weight can tend to be
      // too high, even when one or two parts may be missing (Ex: a 6 looks remarkably like an 8, but we should
      // not even begin to consider it as an 8. We zero the weight for 8 if fewer than 7 features are confirmed
      // not present
      if (countOff > 8) {
        is8 = 0.0;
      }
      
      // Typically, if we can see 0, 5 , and 6, and there's a horizontal mid-height line, then we're looking at an 8
      if (is6 + is0 + is5 > is8 * 1.5 && f > 0.1) {
        is8 += (is6 + is0 + is5) * 0.24;
      }
      
      // If there's vertical lines in the top left and bottom right, we're probably not looking at a 1
      if (fg + p > .9) {
        is1 = 0;
      }
      
      boolean leftVerts = fg + h > 0.9; // is there a vertical line on the left
      boolean rightVerts = m + p > 1; // is there a vertical line on the right
      
      // Remove the chance that any numbers that definitely do not include those features are considered
      if (leftVerts) {
        is3 = 0;
        is5 = 0;
        is7 = 0;
      } else if (rightVerts) {
        is5 *= .7;
        is6 = 0;
      }
      
      // If there's vertical lines running over both edges, it probably isn't any of these numbers
      if (r + s > 0.9) {
        is1 = 0;
        is2 = 0;
        is3 = 0;
        is7 = 0;
      }
      
      // If 8 seems to be more activated than 0, but there's only a diagonal line "/" and not a "\",
      // we're probably looking at a 0 with a slash and not an 8.
      if (is8 > is0 && e > 0.5 && n < 0.2) {
        is0 += is8 * 0.5;
      }


      // Display all of the weights
      System.out.println("1: " + is1);
      System.out.println("2: " + is2);
      System.out.println("3: " + is3);
      System.out.println("4: " + is4);
      System.out.println("5: " + is5);
      System.out.println("6: " + is6);
      System.out.println("7: " + is7);
      System.out.println("8: " + is8);
      System.out.println("9: " + is9);
      System.out.println("0: " + is0);
      
      // Figure out which one has the highest weight and display the result in console
      ArrayList<Double> weights = new ArrayList<>();
      weights.add(is0);
      weights.add(is1);
      weights.add(is2);
      weights.add(is3);
      weights.add(is4);
      weights.add(is5);
      weights.add(is6);
      weights.add(is7);
      weights.add(is8);
      weights.add(is9);
      int highestIdx = 0;
      for (int currIdx = 0; currIdx < 10; currIdx += 1) {
        if (weights.get(currIdx) > weights.get(highestIdx)) {
          highestIdx = currIdx;
        }
      }
      System.out.println("Predicted answer: " + highestIdx);

    }
    else {
      // If we don't have anything on screen, just make the VisionPanel black
      g.setColor(Color.BLACK);
      g.fillRect(0, 0, PaintPanel.SCALE * 50, PaintPanel.SCALE * 50);
    }

  }

}
