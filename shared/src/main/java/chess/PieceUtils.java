package chess;

import java.util.Map;

final class PieceUtils {

  public static final Map<Character, ChessPiece.PieceType> CHAR_TO_TYPE_MAP = Map.of(
      'p', ChessPiece.PieceType.PAWN,
      'n', ChessPiece.PieceType.KNIGHT,
      'r', ChessPiece.PieceType.ROOK,
      'q', ChessPiece.PieceType.QUEEN,
      'k', ChessPiece.PieceType.KING,
      'b', ChessPiece.PieceType.BISHOP);

  public static ChessPiece[][] loadBoard(String boardText) {
    final var board = new ChessPiece[9][9]; 
    int row = 8;
    int column = 1;
    for (var c : boardText.toCharArray()) {
      switch (c) {
        case '\n' -> {
          column = 1;
          row--;
        }
        case ' ' -> column++;
        case '|' -> {
        }
        default -> {
          ChessGame.TeamColor color = Character.isLowerCase(c) ? ChessGame.TeamColor.BLACK
              : ChessGame.TeamColor.WHITE;
          final var type = CHAR_TO_TYPE_MAP.get(Character.toLowerCase(c));
          final var position = new ChessPosition(row, column);
          final var piece = new ChessPiece(color, type);
          board[position.getRow()][position.getColumn()] = piece;
          column++;
        }
      }
    }
    return board;
  }
}
