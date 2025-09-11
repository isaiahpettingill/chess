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
public record ChessPiece(TeamColor pieceColor, PieceType type) {
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
    return pieceColor;
  }

  /**
   * @return which type of chess piece this piece is
   */
  public PieceType getPieceType() {
    return type;
  }

  /**
   * Calculates all the positions a chess piece can move to
   * Does not take into account moves that are illegal due to leaving the king in
   * danger
   *
   * @return Collection of valid moves
   */
  public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
    var moves = switch (type) {
      case KING -> kingMoves(myPosition, board, this);
      case QUEEN -> queenMoves(myPosition, board, this);
      case PAWN -> pawnMoves(myPosition, board, this);
      case KNIGHT -> knightMoves(myPosition, board, this);
      case BISHOP -> bishopMoves(myPosition, board, this);
      case ROOK -> rookMoves(myPosition, board, this);
    };
    return moves.map(x -> new ChessMove(myPosition, x)).collect(Collectors.toSet());
  }

  public String toString() {
    return switch (type) {
      case KING -> (pieceColor == TeamColor.WHITE ? "K" : "k");
      case QUEEN -> (pieceColor == TeamColor.WHITE ? "Q" : "q");
      case BISHOP -> (pieceColor == TeamColor.WHITE ? "B" : "b");
      case ROOK -> (pieceColor == TeamColor.WHITE ? "R" : "r");
      case PAWN -> (pieceColor == TeamColor.WHITE ? "P" : "p");
      case KNIGHT -> (pieceColor == TeamColor.WHITE ? "K" : "k");
    };
  }
}
