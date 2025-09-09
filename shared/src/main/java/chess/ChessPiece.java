package chess;

import java.util.Collection;
import chess.ChessGame.TeamColor;
import java.util.HashSet;
import java.util.Arrays;
import java.util.stream.Stream;
import java.util.stream.Collectors;

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

  private Collection<ChessMove> _getMovesFromPositions(ChessPosition start, int[][] positions) {
    Stream<int[]> streamed = Stream.of(positions);
    return streamed.filter(c -> (c[0] > 0 && c[0] < 8 && c[1] > 0 && c[0] < 8))
        .map(c -> new ChessMove(start, new ChessPosition(c[0] + 1, c[1] + 1)))
        .collect(Collectors.toSet());
  }

  private Collection<ChessMove> _getKingMoves(ChessPosition position) {
    int x = position.getZeroRow();
    int y = position.getZeroColumn();
    int[][] positions = { { x + 1, y }, { x - 1, y }, { x, y - 1 },
        { x, y + 1 }, { x - 1, y - 1 }, { x + 1, y + 1 },
        { x - 1, y + 1 }, { x + 1, y - 1 } };
    return _getMovesFromPositions(position, positions);
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
