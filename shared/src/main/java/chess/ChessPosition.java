package chess;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {
    private int _row;
    private int _column;
    
    public ChessPosition(int row, int col) {
      _row = row;
      _column = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
      return _row;
    }

    public int getZeroRow(){
      return _row - 1;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
      return _column;
    }

    public int getZeroColumn() {
      return _column - 1;
    }
}
