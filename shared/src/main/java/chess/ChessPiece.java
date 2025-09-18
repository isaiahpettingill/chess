package chess;

import java.util.Collection;
import chess.ChessGame.TeamColor;
import java.util.stream.Collectors;
import static chess.ChessRules.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
  private final TeamColor _color;
  private final PieceType _type;

  public ChessPiece(TeamColor pieceColor, PieceType type){
    this._color = pieceColor;
    this._type = type;
  }
  
  /**
   * The various different chess piece options
   */
  public enum PieceType {
    KING,
    QUEEN,
    BISHOP,
    KNIGHT,
    ROOK,
    PAWN
  }

  /**
   * @return Which team this chess piece belongs to
   */
  public ChessGame.TeamColor getTeamColor() {
    return _color;
  }

  /**
   * @return which type of chess piece this piece is
   */
  public PieceType getPieceType() {
    return _type;
  }

  /**
   * Calculates all the positions a chess piece can move to
   * Does not take into account moves that are illegal due to leaving the king in
   * danger
   *
   * @return Collection of valid moves
   */
  public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
    var moves = switch (_type) {
      case KING -> kingMoves(myPosition, board, this);
      case QUEEN -> queenMoves(myPosition, board, this);
      case PAWN -> pawnMoves(myPosition, board, this);
      case KNIGHT -> knightMoves(myPosition, board, this);
      case BISHOP -> bishopMoves(myPosition, board, this);
      case ROOK -> rookMoves(myPosition, board, this);
    };
    return moves.collect(Collectors.toUnmodifiableSet());
  }

  public String toString() {
    return switch (_type) {
      case KING -> (_color == TeamColor.WHITE ? "K" : "k");
      case QUEEN -> (_color == TeamColor.WHITE ? "Q" : "q");
      case BISHOP -> (_color == TeamColor.WHITE ? "B" : "b");
      case ROOK -> (_color == TeamColor.WHITE ? "R" : "r");
      case PAWN -> (_color == TeamColor.WHITE ? "P" : "p");
      case KNIGHT -> (_color == TeamColor.WHITE ? "N" : "n");
    };
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((_color == null) ? 0 : _color.hashCode());
    result = prime * result + ((_type == null) ? 0 : _type.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ChessPiece other = (ChessPiece) obj;
    if (_color != other._color)
      return false;
    if (_type != other._type)
      return false;
    return true;
  }

  
}
