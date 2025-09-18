package chess;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public record ChessPosition(int row, int column) {
  

  /**
   * @return which row this position is in
   *         1 codes for the bottom row
   */
  public int getRow() {
    return row;
  }

  public int getZeroRow() {
    return row - 1;
  }

  /**
   * @return which column this position is in
   *         1 codes for the left row
   */
  public int getColumn() {
    return column;
  }

  public int getZeroColumn() {
    return column - 1;
  }

  public boolean isInRange() {
    return row > 0 && row < 9 && column > 0 && column < 9;
  }

  public ChessPosition add(int x, int y) {
    return new ChessPosition(row + y, column + x);
  }

  public ChessPosition(ChessPosition position) {
    this(position.getRow(), position.getColumn());
  }

  public String toString(){
    return "(" + column + "," + row + ")";
  }
}
