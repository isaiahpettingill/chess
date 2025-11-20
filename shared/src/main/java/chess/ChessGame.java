package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import static ui.EscapeSequences.*;
import com.google.gson.Gson;

import chess.ChessBoard.PieceWithPosition;
import chess.ChessPiece.PieceType;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public final class ChessGame {
  private TeamColor teamTurn;
  private ChessBoard board;
  private ChessHistory history;

  public ChessGame() {
    teamTurn = TeamColor.WHITE;
    board = new ChessBoard();
    board.resetBoard();
    history = new ChessHistory();
  }

  /**
   * @return Which team's turn it is
   */
  public TeamColor getTeamTurn() {
    return teamTurn;
  }

  /**
   * Set's which teams turn it is
   *
   * @param team the team whose turn it is
   */
  public void setTeamTurn(TeamColor team) {
    teamTurn = team;
  }

  /**
   * Enum identifying the 2 possible teams in a chess game
   */
  public enum TeamColor {
    WHITE,
    BLACK
  }

  public TeamColor otherColor(TeamColor color) {
    return switch (color) {
      case WHITE -> TeamColor.BLACK;
      case BLACK -> TeamColor.WHITE;
    };
  }

  /**
   * Gets a valid moves for a piece at the given location
   *
   * @param startPosition the piece to get valid moves for
   * @return Set of valid moves for requested piece, or null if no piece at
   *         startPosition
   */
  public Collection<ChessMove> validMoves(ChessPosition startPosition) {
    final var piece = board.getPiece(startPosition);
    if (piece == null) {
      return Set.of();
    }
    final var moves = piece.pieceMoves(board, startPosition)
        .stream()
        .filter(x -> !kingWouldBeInCheck(x, piece.getTeamColor()))
        .collect(Collectors.toUnmodifiableSet());
    return moves;
  }

  /**
   * Makes a move in a chess game
   *
   * @param move chess move to perform
   * @throws InvalidMoveException if move is invalid
   */
  public void makeMove(ChessMove move) throws InvalidMoveException {
    final var piece = board.getPiece(move.getStartPosition());
    if (piece == null) {
      throw new InvalidMoveException("There is no piece to move");
    }
    if (piece.getTeamColor() != getTeamTurn()) {
      throw new InvalidMoveException("Not your turn.");
    }

    final var valid = validMoves(move.getStartPosition());

    if (!valid.contains(move)) {
      throw new InvalidMoveException("Move is not valid for piece");
    } else if (isInCheck(piece.getTeamColor()) && kingWouldBeInCheck(move, piece.getTeamColor())) {
      throw new InvalidMoveException("King is still in check");
    } else {
      board.movePiece(move);
      promotePiece(move, piece.getTeamColor());
      teamTurn = otherColor(teamTurn);
      history.saveMove(board, move);
    }
  }

  private boolean kingWouldBeInCheck(ChessMove move, TeamColor teamColor) {
    final var future = new ChessBoard(board);
    future.movePiece(move);
    return isInCheck(teamColor, future);
  }

  private void promotePiece(ChessMove move, TeamColor teamColor) {
    if (move.getPromotionPiece() == null) {
      return;
    }
    board.addPiece(move.getEndPosition(), new ChessPiece(teamColor, move.getPromotionPiece()));
  }

  /**
   * Determines if the given team is in check
   *
   * @param teamColor which team to check for check
   * @return True if the specified team is in check
   */
  public boolean isInCheck(TeamColor teamColor, ChessBoard board) {
    if (teamColor == null || board == null) {
      return false;
    }
    final var king = switch (teamColor) {
      case WHITE -> getWhiteKing(board);
      case BLACK -> getBlackKing(board);
    };
    final var isInCheck = board.allMovesIncludingAttackKing(otherColor(teamColor))
        .map(x -> x.getEndPosition())
        .anyMatch(x -> king.pos().equals(x));
    return isInCheck;
  }

  public boolean isInCheck(TeamColor teamColor) {
    return isInCheck(teamColor, board);
  }

  /**
   * Determines if the given team is in checkmate
   *
   * @param teamColor which team to check for checkmate
   * @return True if the specified team is in checkmate
   */
  public boolean isInCheckmate(TeamColor teamColor) {
    var couldBeCheckmate = isInCheck(teamColor);
    if (couldBeCheckmate) {
      final var moves = board.allMovesIncludingAttackKing(teamColor).collect(Collectors.toUnmodifiableSet());
      for (var move : moves) {
        if (!kingWouldBeInCheck(move, teamColor)) {
          couldBeCheckmate = false;
          break;
        }
      }
    }
    return couldBeCheckmate;
  }

  /**
   * Determines if the given team is in stalemate, which here is defined as having
   * no valid moves while not in check.
   *
   * @param teamColor which team to check for stalemate
   * @return True if the specified team is in stalemate, otherwise false
   */
  public boolean isInStalemate(TeamColor teamColor) {
    final var pieces = board.piecesAndPositions()
        .collect(Collectors.toUnmodifiableSet());
    final var allKings = pieces.stream()
        .map(x -> x.piece().getPieceType())
        .allMatch(x -> x == ChessPiece.PieceType.KING);
    if (allKings) {
      return true;
    }

    final var sum = pieces.stream()
        .filter(x -> x.piece().getTeamColor() == teamColor)
        .mapToInt(x -> x.piece().pieceMoves(board, x.pos()).size())
        .sum();
    if (sum == 0) {
      return true;
    }
    final var onlyKingIsThere = pieces.stream()
        .filter(x -> x.piece().getTeamColor() == teamColor)
        .count() == 1;
    if (onlyKingIsThere) {
      final var king = switch (teamColor) {
        case WHITE -> getWhiteKing(board);
        case BLACK -> getBlackKing(board);
      };
      final var kingMoves = king.piece().pieceMoves(board, king.pos());
      final var validMoves = kingMoves.stream().filter(x -> !kingWouldBeInCheck(x, teamColor)).count();
      return validMoves == 0;
    }
    return false;
  }

  /**
   * Sets this game's chessboard with a given board
   * 
   * @param board the new board to use
   */
  public void setBoard(ChessBoard board) {
    this.board = board;
  }

  private PieceWithPosition getWhiteKing(ChessBoard board) {
    return board.piecesAndPositions()
        .filter(x -> x.piece().getPieceType() == PieceType.KING
            && x.piece().getTeamColor() == TeamColor.WHITE)
        .findFirst()
        .get();
  }

  private PieceWithPosition getBlackKing(ChessBoard board) {
    return board.piecesAndPositions()
        .filter(x -> x.piece().getPieceType() == PieceType.KING
            && x.piece().getTeamColor() == TeamColor.BLACK)
        .findFirst()
        .get();
  }

  /**
   * Gets the current chessboard
   *
   * @return the chessboard
   */
  public ChessBoard getBoard() {
    return board;
  }

  public int hashCode() {
    return Objects.hash(board, teamTurn);
  }

  public boolean equals(Object object) {
    return (object == this) ||
        (object instanceof ChessGame cg)
            && (cg.getBoard().equals(board))
            && (cg.getTeamTurn().equals(teamTurn));
  }

  private static void whiteSquare(StringBuilder builder) {
    builder.append(SET_BG_COLOR_WHITE);
    builder.append(SET_TEXT_COLOR_BLACK);
  }

  private static void blackSquare(StringBuilder builder) {
    builder.append(SET_BG_COLOR_BLACK);
    builder.append(SET_TEXT_COLOR_WHITE);
  }

  public static void rotateBoard180Degrees(ChessPiece[][] theboard) {
    for (int i = 0; i < 2; i++) {
      // transpose. Yeah, I know this could be done more efficiently but I don't care.
      for (int row = 1; row < 9; row++) {
        for (int col = row + 1; col < 9; col++) {
          final var temp = theboard[row][col];
          theboard[row][col] = theboard[col][row];
          theboard[col][row] = temp;
        }
      }
      // reverse each row. Yeah, I'm sure there's a better way.
      for (int row = 1; row < 9; row++) {
        ArrayList<ChessPiece> therow = new ArrayList<>(Arrays.asList(theboard[row]));
        therow.add(null);
        Collections.reverse(therow);
        therow.removeLast();
        theboard[row] = therow.toArray(new ChessPiece[therow.size()]);
      }
    }
  }

  private static String getRowLabel(int row, boolean reverse) {
    int label = reverse ? (9 - row) : row;
    return label + "  ";
  }

  private static final String COLUMN_LABELS_NORMAL = "    a  b  c  d  e  f  g  h  \n";
  private static final String COLUMN_LABELS_REVERSE = "    h  g  f  e  d  c  b  a  \n";

  public String toPrettyString(boolean reverse) {
    return toPrettyString(reverse, Optional.empty());
  }

  public String toPrettyString(boolean reverse, Optional<ChessPosition> move) {
    final var hasMove = move.isPresent();
    var builder = new StringBuilder();
    builder.append("\n\n");
    builder.append(reverse ? COLUMN_LABELS_REVERSE : COLUMN_LABELS_NORMAL);
    var gson = new Gson();
    var theboard = gson.fromJson(gson.toJson(board.rawBoard()), ChessPiece[][].class);
    if (reverse) {
      rotateBoard180Degrees(theboard);
    }
    for (int row = 8; row >= 1; row--) {
      builder.append(RESET_BG_COLOR);
      builder.append(RESET_TEXT_COLOR);
      builder.append(getRowLabel(row, reverse));
      for (int col = 1; col < 9; col++) {
        boolean isWhiteSquare = ((row + col) % 2) == 1;
        final var currentPosition = reverse ?  new ChessPosition(9 - row, 9 - col) : new ChessPosition(row, col);
        final var isValidMove = hasMove
            && validMoves(move.get()).contains(
                new ChessMove(move.get(), currentPosition));
        if (hasMove && currentPosition.equals(move.get())) {
          builder.append(SET_BG_COLOR_YELLOW);
          builder.append(SET_TEXT_COLOR_DARK_GREY);
          builder.append(SET_TEXT_BLINKING);
        } else if (isWhiteSquare) {
          if (isValidMove) {
            builder.append(SET_BG_COLOR_GREEN);
            builder.append(SET_TEXT_COLOR_DARK_GREY);
          } else {
            whiteSquare(builder);
          }
        } else {
          if (isValidMove) {
            builder.append(SET_BG_COLOR_DARK_GREEN);
            builder.append(SET_TEXT_COLOR_LIGHT_GREY);
          } else {
            blackSquare(builder);
          }
        }
        var piece = theboard[row][col];
        builder.append(piece == null ? "   " : piece.toPrettyString());
        builder.append(RESET_TEXT_BLINKING);
      }
      builder.append(RESET_BG_COLOR);
      builder.append(RESET_TEXT_COLOR);
      builder.append("  " + getRowLabel(row, reverse));
      builder.append('\n');
    }
    builder.append(reverse ? COLUMN_LABELS_REVERSE : COLUMN_LABELS_NORMAL);

    return builder.toString();
  }
}
