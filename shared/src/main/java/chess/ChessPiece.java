package chess;

import java.util.Collection;
import chess.ChessGame.TeamColor;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.Iterator;
import java.util.List;
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

    var positions = switch (type) {
      case KING -> _getKingMoves(myPosition, board);
      case QUEEN -> _getQueenMoves(myPosition, board);
      case PAWN -> _getPawnMoves(myPosition, board);
      case KNIGHT -> _getKnightMoves(myPosition, board);
      case BISHOP -> _getBishopMoves(myPosition, board);
      case ROOK -> _getRookMoves(myPosition, board);
    };
    var moves = _getMovesFromPositions(myPosition, positions, board);
    return moves.collect(Collectors.toSet());

  }

  private Stream<ChessMove> _getMovesFromPositions(ChessPosition start, Iterator<int[]> positions, ChessBoard board) {
    Stream<int[]> streamed = Stream.of(positions);
    return streamed.filter(c -> (c[0] > 0 && c[0] < 8 && c[1] > 0 && c[0] < 8))
        .map(c -> new ChessPosition(c[0] + 1, c[1] + 1))
        .filter(c -> board.getPiece(c).getTeamColor() != pieceColor)
        .map(c -> new ChessMove(start, c));
  }

  private Iterable<int[]> _getKingMoves(ChessPosition position, ChessBoard board) {
    int x = position.getZeroRow();
    int y = position.getZeroColumn();
    int[][] positions = { { x + 1, y }, { x - 1, y }, { x, y - 1 },
        { x, y + 1 }, { x - 1, y - 1 }, { x + 1, y + 1 },
        { x - 1, y + 1 }, { x + 1, y - 1 } };
    return List.of(positions);
  }

  private Iterator<ChessMove> _getQueenMoves(ChessPosition position, ChessBoard board) {

    return new HashSet<ChessMove>();
  }

  private Iterator<ChessMove> _getKnightMoves(ChessPosition position, ChessBoard board) {

    return new HashSet<ChessMove>();
  }

  private Stream<ChessMove> _getRookMoves(ChessPosition position, ChessBoard board) {

    return new HashSet<ChessMove>();
  }

  private Stream<ChessMove> _getPawnMoves(ChessPosition position, ChessBoard board) {

    return new HashSet<ChessMove>();
  }

  private Stream<ChessMove> _getBishopMoves(ChessPosition position, ChessBoard board) {

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
