package main;


/**
 * Optional interface to prevent null referencing and allow "Empty" VisionBoards.
 */
public interface OptionalVisionBoard {

  /**
   * Returns if this OptionalVisionBoard can be extracted to a VisionBoard.
   * @return if this OptionalVisionBoard is a VisionBoard
   */
  boolean isVisionBoard();
  
  /**
   * Extracts the VisionBoard from this OptionalVisionBoard.
   * @return the VisionBoard stored in this OptionalVisionBoard
   * @throws IllegalStateException if this OptionalVisionBoard is Empty and cannot be extracted.
   */
  VisionBoard extract();
  
}
