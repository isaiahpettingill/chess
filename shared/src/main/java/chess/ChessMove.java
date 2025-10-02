package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {
  private ChessPosition _startPosition;
  private ChessPosition _endPosition;
  private ChessPiece.PieceType _promotionPiece;

  public ChessMove(ChessPosition startPosition, ChessPosition endPosition) {
    _startPosition = startPosition;
    _endPosition = endPosition;
    _promotionPiece = null;
  }

  public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
      ChessPiece.PieceType promotionPiece) {
    _startPosition = startPosition;
    _endPosition = endPosition;
    _promotionPiece = promotionPiece;
  }

  /**
   * @return ChessPosition of starting location
   */
  public ChessPosition getStartPosition() {
    return _startPosition;
  }

  /**
   * @return ChessPosition of ending location
   */
  public ChessPosition getEndPosition() {
    return _endPosition;
  }

  /**
   * Gets the type of piece to promote a pawn to if pawn promotion is part of this
   * chess move
   *
   * @return Type of piece to promote a pawn to, or null if no promotion
   */
  public ChessPiece.PieceType getPromotionPiece() {
    return _promotionPiece;
  }

  public String toString() {
    return _startPosition.toString() 
        + (_promotionPiece == null ? "->" : "-[" + _promotionPiece.toString() + "]->")
        + _endPosition.toString();
  }

  public boolean equals(Object o) {
    if (o == this)
      return true;
    if (o == null || !(o instanceof ChessMove))
      return false;
    var m = (ChessMove) o;
    return m.getStartPosition().equals(getStartPosition())
        && m.getEndPosition().equals(getEndPosition())
        && (m.getPromotionPiece() == null || m.getPromotionPiece().equals(_promotionPiece));
  }

  public int hashCode() {
    return Objects.hash(_startPosition, _endPosition, _promotionPiece);
  }
}
