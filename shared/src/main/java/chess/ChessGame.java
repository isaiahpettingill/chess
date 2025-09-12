package chess;

import java.util.Collection;
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
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
      var piece = _board.getPiece(startPosition);
      var moves = piece.pieceMoves(_board, startPosition); 
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
          throw new InvalidMoveException();
        }
        else if(isInCheck(piece.getTeamColor())) {
            
        }
        else {
          _movePiece(move);
        }
    }

    private void _movePiece(ChessMove move){
      var piece = _board.getPiece(move.getStartPosition());
      _board.addPiece(move.getEndPosition(), piece);
      _board.addPiece(move.getStartPosition(), null);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
      throw new RuntimeException("Not implemented"); //TODO: implement check
    }
    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented"); //TODO: implement checkmate
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
      var sum = _board.allPieces()
         .filter(x -> x.piece().getTeamColor() == teamColor)
         .mapToInt(x -> x.piece().pieceMoves(_board, x.pos()).size())
         .takeWhile(x -> x == 0)
         .limit(1) 
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

    private boolean _pieceCanBeKilledAt(TeamColor teamColor, ChessPosition position){
      Stream<ChessMove> moves = _board.allPieces().filter(x -> x.piece().getTeamColor() == teamColor)
        .flatMap(x -> validMoves(x.pos()).stream());
      var canBeKilled = moves.map(x -> x.getEndPosition()).filter(x -> position.equals(x)).count() == 0;
      return canBeKilled;
    }
}
