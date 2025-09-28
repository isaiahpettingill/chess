package chess;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
  private TeamColor _teamTurn;
  private ChessBoard _board;

  public ChessGame() {
    _teamTurn = TeamColor.WHITE;
    _board = new ChessBoard();
  }

  /**
   * @return Which team's turn it is
   */
  public TeamColor getTeamTurn() {
    return _teamTurn;
  }

  /**
   * Set's which teams turn it is
   *
   * @param team the team whose turn it is
   */
  public void setTeamTurn(TeamColor team) {
    _teamTurn = team;
  }

  /**
   * Enum identifying the 2 possible teams in a chess game
   */
  public enum TeamColor {
    WHITE,
    BLACK
  }

  /**
   * Gets a valid moves for a piece at the given location
   *
   * @param startPosition the piece to get valid moves for
   * @return Set of valid moves for requested piece, or null if no piece at
   *         startPosition
   */
  public Collection<ChessMove> validMoves(ChessPosition startPosition) {
    var piece = _board.getPiece(startPosition);
    if (piece == null)
      return Set.of();
    var moves = piece.pieceMoves(_board, startPosition);
      // .stream()
      // .filter(x -> !_kingWouldBeInCheck(x, piece.getTeamColor()))
      // .collect(Collectors.toUnmodifiableSet());
    return moves;
  }

  /**
   * Makes a move in a chess game
   *
   * @param move chess move to perform
   * @throws InvalidMoveException if move is invalid
   */
  public void makeMove(ChessMove move) throws InvalidMoveException {
    var valid = validMoves(move.getStartPosition());
    var piece = _board.getPiece(move.getStartPosition());

    if (!valid.contains(move)) {
      throw new InvalidMoveException("Move is not valid for piece");
    } else if (isInCheck(piece.getTeamColor()) && _kingWouldBeInCheck(move, piece.getTeamColor())) {
      throw new InvalidMoveException("King is still in check");
    } else {
      _board.movePiece(move);
      _promotePiece(move, piece.getTeamColor());
    }
  }

  private boolean _kingWouldBeInCheck(ChessMove move, TeamColor teamColor) {
    var future = new ChessBoard(_board);
    future.movePiece(move);
    return isInCheck(teamColor, future);
  }

  private void _promotePiece(ChessMove move, TeamColor teamColor) {
    _board.addPiece(move.getEndPosition(), new ChessPiece(teamColor, move.getPromotionPiece()));
  }

  /**
   * Determines if the given team is in check
   *
   * @param teamColor which team to check for check
   * @return True if the specified team is in check
   */
  public boolean isInCheck(TeamColor teamColor, ChessBoard board) {
    var kingPos = _board.allPieces()
        .filter(x -> x.piece().getPieceType() == ChessPiece.PieceType.KING && x.piece().getTeamColor() == teamColor)
        .map(x -> x.pos())
        .findFirst().orElseThrow();
    return _pieceCanBeKilledAt(teamColor, kingPos);
  }

  public boolean isInCheck(TeamColor teamColor) {
    return isInCheck(teamColor, _board);
  }

  /**
   * Determines if the given team is in checkmate
   *
   * @param teamColor which team to check for checkmate
   * @return True if the specified team is in checkmate
   */
  public boolean isInCheckmate(TeamColor teamColor) {
    boolean checkmate = false;
    if (isInCheck(teamColor)) {
      var moves = _board.allPieces()
          .filter(x -> x.piece().getPieceType() == ChessPiece.PieceType.KING && x.piece().getTeamColor() == teamColor)
          .flatMap(x -> validMoves(x.pos()).stream());
      checkmate = moves.count() == 0;
    }
    return checkmate;
  }

  /**
   * Determines if the given team is in stalemate, which here is defined as having
   * no valid moves while not in check.
   *
   * @param teamColor which team to check for stalemate
   * @return True if the specified team is in stalemate, otherwise false
   */
  public boolean isInStalemate(TeamColor teamColor) {
    var pieces = _board.allPieces();
    var allKings = pieces.map(x -> x.piece().getPieceType()).allMatch(x -> x == ChessPiece.PieceType.KING);
    if (allKings)
      return true;

    var sum = pieces
        .filter(x -> x.piece().getTeamColor() == teamColor)
        .mapToInt(x -> x.piece().pieceMoves(_board, x.pos()).size())
        .sum();
    return sum == 0;
  }

  /**
   * Sets this game's chessboard with a given board
   *
   * @param board the new board to use
   */
  public void setBoard(ChessBoard board) {
    _board = board;
  }

  /**
   * Gets the current chessboard
   *
   * @return the chessboard
   */
  public ChessBoard getBoard() {
    return _board;
  }

  private boolean _pieceCanBeKilledAt(TeamColor teamColor, ChessPosition position) {
    Stream<ChessMove> moves = _board.allPieces().filter(x -> x.piece().getTeamColor() == teamColor)
        .flatMap(x -> validMoves(x.pos()).stream());
    var canBeKilled = moves.map(x -> x.getEndPosition()).filter(x -> position.equals(x)).count() == 0;
    return canBeKilled;
  }

  public int hashCode() {
    return Objects.hash(_board, _teamTurn);
  }

  public boolean equals(Object object) {
    return (object == this) ||
        (object instanceof ChessGame cg)
            && (cg.getBoard().equals(_board))
            && (cg.getTeamTurn().equals(_teamTurn));
  }
}
