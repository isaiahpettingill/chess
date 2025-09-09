package chess;

import java.util.Collection;
import chess.ChessGame.TeamColor;
import java.util.HashSet;

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
      case KING -> _getKingMoves(myPosition);
      case QUEEN -> _getQueenMoves(myPosition);
      case PAWN -> _getPawnMoves(myPosition);
      case KNIGHT -> _getKnightMoves(myPosition);
      case BISHOP -> _getBishopMoves(myPosition);
      case ROOK -> _getRookMoves(myPosition);
      default -> new HashSet<ChessMove>();
    };
    return moves;

  }

  private Collection<ChessMove> _getKingMoves(ChessPosition position) {
    return new HashSet<ChessMove>();
  }

  private Collection<ChessMove> _getQueenMoves(ChessPosition position) {

    return new HashSet<ChessMove>();
  }

  private Collection<ChessMove> _getKnightMoves(ChessPosition position) {

    return new HashSet<ChessMove>();
  }

  private Collection<ChessMove> _getRookMoves(ChessPosition position) {

    return new HashSet<ChessMove>();
  }

  private Collection<ChessMove> _getPawnMoves(ChessPosition position) {

    return new HashSet<ChessMove>();
  }

  private Collection<ChessMove> _getBishopMoves(ChessPosition position) {

    return new HashSet<ChessMove>();
  }

  public String getBoardCode() {
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
