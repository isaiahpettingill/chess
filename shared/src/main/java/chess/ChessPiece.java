package chess;

import java.util.Collection;
import chess.ChessGame.TeamColor;
import static ui.EscapeSequences.*;

import java.util.stream.Collectors;
import static chess.ChessRules.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public final class ChessPiece {
  private final TeamColor color;
  private final PieceType type;

  public ChessPiece(TeamColor pieceColor, PieceType type) {
    this.color = pieceColor;
    this.type = type;
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
    return color;
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
    return getPieceMoves(type, color, board, myPosition)
        .collect(Collectors.toUnmodifiableSet());
  }

    /**
   * Calculates all the positions a chess piece can move to
   * Includes even moves that would kill a king (not valid, but used for checking if king is in check)
   *
   * @return Collection of valid moves
   */
  public Collection<ChessMove> pieceMovesRaw(ChessBoard board, ChessPosition myPosition) {
    return getPieceMovesRaw(type, color, board, myPosition)
        .collect(Collectors.toUnmodifiableSet());
  }

  public String toString() {
    if (type == null || color == null) {
      return " ";
    }

    var caps = switch (type) {
      case KING -> "K";
      case QUEEN -> "Q";
      case BISHOP -> "B";
      case ROOK -> "R";
      case PAWN -> "P";
      case KNIGHT -> "N";
    };
    if (color.equals(TeamColor.BLACK)) {
      return caps.toLowerCase();
    }
    return caps;
  }

  public String toPrettyString() {
    if (type == null || color == null) {
      return " ";
    }
    final var black = color.equals(TeamColor.BLACK);
    var caps = switch (type) {
      case KING -> black ? BLACK_KING : WHITE_KING;
      case QUEEN -> black ? BLACK_QUEEN : WHITE_QUEEN;
      case BISHOP -> black ? BLACK_BISHOP : WHITE_BISHOP;
      case ROOK -> black ? BLACK_ROOK : WHITE_ROOK;
      case PAWN -> black ? BLACK_PAWN : WHITE_PAWN;
      case KNIGHT -> black ? BLACK_KING : WHITE_KNIGHT;
    };
    return caps;
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((color == null) ? 0 : color.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    ChessPiece other = (ChessPiece) obj;
    if (color != other.color) {
      return false;
    }
    if (type != other.type) {
      return false;
    }
    return true;
  }

}
