package chess;

/*
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
  private ChessPiece[][] board;

  private static Boolean isInRange(ChessPosition position)
  {
    var row = position.getRow();
    var col = position.getColumn();
    return row > 0 && row < 8 && col > 0 && col < 8;
  }
  
  public ChessBoard() {
    board = new ChessPiece[8][8];
  }

  /**
   * Adds a chess piece to the chessboard
   *
   * @param position where to add the piece to
   * @param piece    the piece to add
   */
  public void addPiece(ChessPosition position, ChessPiece piece) {
    
  }

  /**
   * Gets a chess piece on the chessboard
   *
   * @param position The position to get the piece from
   * @return Either the piece at the position, or null if no piece is at that
   *         position
   */
  public ChessPiece getPiece(ChessPosition position) {
    if (isInRange(position))
      return board[position.getRow()][position.getColumn()];
    else
      throw new IllegalArgumentException("Your piece would have fallen off the board");
  }

  /**
   * Sets the board to the default starting board
   * (How the game of chess normally starts)
   */
  public void resetBoard() {
    board = new ChessPiece[8][8];
  }
}
