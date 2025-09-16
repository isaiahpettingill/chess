package chess;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Stream;

/*
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
  private ChessPiece[][] _board;

  private static boolean isInRange(ChessPosition position)
  {
    var row = position.getZeroRow();
    var col = position.getZeroColumn();
    return row > 0 && row < 8 && col > 0 && col < 8;
  }
  
  public ChessBoard() {
    _board = new ChessPiece[8][8];
  }

  /**
   * Adds a chess piece to the chessboard
   *
   * @param position where to add the piece to
   * @param piece    the piece to add
   */
  public void addPiece(ChessPosition position, ChessPiece piece) {
    if (isInRange(position)){
      _board[position.getZeroRow()][position.getZeroColumn()] = piece;
    }
    else {
      throw new IllegalArgumentException("Target coordinates are off the board");
    }     
  }

  /**
   * Gets a chess piece on the chessboard
   *
   * @param position The position to get the piece from
   * @return Either the piece at the position, or null if no piece is at that
   *         position
   */
  public ChessPiece getPiece(ChessPosition position){
    if (isInRange(position)) {
      return _board[position.getZeroRow()][position.getZeroColumn()];
    } else {
      throw new IllegalArgumentException("Target coordinates are off the board");
    }
  }

  public record PieceWithPosition(ChessPiece piece, ChessPosition pos){}

  public Stream<PieceWithPosition> allPieces(){
    var pieces = new ArrayList<PieceWithPosition>();
    for (int x = 0; x < 8; x++) {
      for (int y = 0; y < 8; y++) {
        var piece = _board[x][y];
        if (piece != null) {
          pieces.add(new PieceWithPosition(piece, new ChessPosition(x + 1, y + 1)));
        }
      }
    }
    return pieces.stream();
  }

  /**
   * Sets the board to the default starting board
   * (How the game of chess normally starts)
   */
  public void resetBoard() {
    _board = new ChessPiece[8][8];
  }

  public String toString(){
    var builder = new StringBuilder();
    for (var row : _board){
      builder.append('|');
      for (var piece : row){
        builder.append(piece == null ? ' ' : piece.toString());
        builder.append('|');
      }
      builder.append('\n');
    }
    return builder.toString();
  }

  public int hashCode(){
    return _board.hashCode();
  }

  public boolean equals(Object o){
    if (o == this) return true;
    if (o == null || !(o instanceof ChessBoard)) return false;
    var b = (ChessBoard) o;
    return b._board.equals(_board);
  }
}
