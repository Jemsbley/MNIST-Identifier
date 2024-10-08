package main;

/**
 * Represents an "Empty" VisionBoard. Used to prevent Null referencing.
 */
public class EmptyVisionBoard implements OptionalVisionBoard{

  /**
   * An EmptyVisionBoard cannot be extracted to a VisionBoard.
   * @return false
   */
  public boolean isVisionBoard() {
    return false;
  }

  /**
   * An EmptyVisionBoard cannot be extracted to a VisionBoard.
   * @throws IllegalStateException to prevent attempted extraction
   */
  public VisionBoard extract() {
    throw new IllegalStateException("Cannot extract non-existent board");
  }

}
