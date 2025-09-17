package chess;

import java.util.Map;

class PieceUtils {

  public static final Map<Character, ChessPiece.PieceType> CHAR_TO_TYPE_MAP = Map.of(
      'p', ChessPiece.PieceType.PAWN,
      'n', ChessPiece.PieceType.KNIGHT,
      'r', ChessPiece.PieceType.ROOK,
      'q', ChessPiece.PieceType.QUEEN,
      'k', ChessPiece.PieceType.KING,
      'b', ChessPiece.PieceType.BISHOP);

  public static ChessBoard loadBoard(String boardText) {
    var board = new ChessBoard();
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
          var type = CHAR_TO_TYPE_MAP.get(Character.toLowerCase(c));
          var position = new ChessPosition(row, column);
          var piece = new ChessPiece(color, type);
          board.addPiece(position, piece);
          column++;
        }
      }
    }
    return board;
  }
}
