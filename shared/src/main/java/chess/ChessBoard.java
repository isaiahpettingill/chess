package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import chess.ChessGame.TeamColor;

/*
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public final class ChessBoard {
  private ChessPiece[][] _board;

  public ChessBoard() {
    _board = new ChessPiece[9][9];
  }

  public ChessBoard(ChessBoard existingBoard) {
    _board = Arrays.stream(existingBoard._board)
        .map(x -> Arrays.copyOf(x, x.length))
        .toArray(ChessPiece[][]::new);
  }

  /**
   * Adds a chess piece to the chessboard
   *
   * @param position where to add the piece to
   * @param piece    the piece to add
   */
  public void addPiece(ChessPosition position, ChessPiece piece) {
    if (position.isInRange()) {
      _board[position.getRow()][position.getColumn()] = piece;
    } else {
      throw new IllegalArgumentException("Target coordinates are off the board");
    }
  }

  public void removePiece(ChessPosition position) {
    if (position.isInRange()) {
      _board[position.getRow()][position.getColumn()] = null;
    } else {
      throw new IllegalArgumentException("Target coordinates are off the board");
    }
  }

  public void movePiece(ChessMove move) {
    var piece = getPiece(move.getStartPosition());
    addPiece(move.getEndPosition(), piece);
    removePiece(move.getStartPosition());
  }

  /**
   * Gets a chess piece on the chessboard
   *
   * @param position The position to get the piece from
   * @return Either the piece at the position, or null if no piece is at that
   *         position
   */
  public ChessPiece getPiece(ChessPosition position) {
    if (position.isInRange()) {
      return _board[position.getRow()][position.getColumn()];
    } else {
      return null;
    }
  }

  public record PieceWithPosition(ChessPiece piece, ChessPosition pos) {
  }

  public Stream<PieceWithPosition> piecesAndPositions() {
    var pieces = new ArrayList<PieceWithPosition>();
    for (int row = 1; row < 9; row++) {
      for (int col = 1; col < 9; col++) {
        var piece = _board[row][col];
        if (piece instanceof ChessPiece) {
          pieces.add(new PieceWithPosition(piece, new ChessPosition(row, col)));
        }
      }
    }
    return pieces.stream();
  }

  public Stream<ChessMove> allMovesIncludingAttackKing(TeamColor color) {
    var pieces = new ArrayList<Stream<ChessMove>>();
    for (int row = 1; row < 9; row++) {
      for (int col = 1; col < 9; col++) {
        var piece = _board[row][col];
        if (piece instanceof ChessPiece) {
          if (piece.getTeamColor() != color)
            continue;
          pieces.add(piece.pieceMovesRaw(this, new ChessPosition(row, col)).stream());
        }
      }
    }
    return pieces.stream().flatMap(x -> x);
  }

  /**
   * Sets the board to the default starting board
   * (How the game of chess normally starts)
   */
  public void resetBoard() {
    this._board = PieceUtils.loadBoard(DEFAULT_BOARD);
  }

  private final static String DEFAULT_BOARD = """
      |r|n|b|q|k|b|n|r|
      |p|p|p|p|p|p|p|p|
      | | | | | | | | |
      | | | | | | | | |
      | | | | | | | | |
      | | | | | | | | |
      |P|P|P|P|P|P|P|P|
      |R|N|B|Q|K|B|N|R|
      """;

  public String toString() {
    var builder = new StringBuilder();
    for (int row = 1; row < 9; row++) {
      builder.append('|');
      for (int col = 1; col < 9; col++) {
        var piece = getPiece(new ChessPosition(row, col));
        builder.append(piece == null ? ' ' : piece.toString());
        builder.append('|');
      }
      builder.append('\n');
    }
    return builder.toString();
  }

  public int hashCode() {
    return Arrays.deepHashCode(_board);
  }

  public boolean equals(Object o) {
    if (o == this)
      return true;
    if (o == null || !(o instanceof ChessBoard))
      return false;
    var b = (ChessBoard) o;
    for (int i = 1; i < 9; i++) {
      for (int j = 1; j < 9; j++) {
        if (b._board[i][j] == _board[i][j])
          continue;
        if (b._board[i][j] == null || _board[i][j] == null)
          return false;
        if (!b._board[i][j].equals(_board[i][j]))
          return false;
      }
    }
    return true;
  }

  public String boardWithAttackSlots(TeamColor teamColor) {
    var builder = new StringBuilder();

    var moves = allMovesIncludingAttackKing(teamColor).map(x -> x.getEndPosition()).collect(Collectors.toSet());

    for (int row = 1; row < 9; row++) {
      builder.append('|');
      for (int col = 1; col < 9; col++) {
        var piece = getPiece(new ChessPosition(row, col));
        builder.append(piece == null ? ' ' : piece.toString());
        builder.append('|');
      }
      builder.append('\t');

      builder.append('|');
      for (int col = 1; col < 9; col++) {
        var piece = getPiece(new ChessPosition(row, col));
        if (moves.contains(new ChessPosition(row, col))) {
          if (piece == null)
            builder.append('.');
          else 
            builder.append('X');
        } else if (piece != null) {
            builder.append(piece.toString());
        } else {
          builder.append(' ');
        }
        builder.append('|');
      }
      builder.append('\n');
    }
    return builder.toString();

  }

}
