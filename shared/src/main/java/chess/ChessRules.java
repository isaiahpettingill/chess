package chess;

import java.util.stream.Stream;

import chess.ChessGame.TeamColor;

final class ChessRules {

  public static Stream<ChessPosition> kingMoves(ChessPosition position, ChessBoard board, ChessPiece piece) {
    return Stream.of(
        _getHorizontalMotion(1, 0, position, board, piece).limit(1),
        _getHorizontalMotion(-1, 0, position, board, piece).limit(1),
        _getHorizontalMotion(0, 1, position, board, piece).limit(1),
        _getHorizontalMotion(0, -1, position, board, piece).limit(1),
        _getHorizontalMotion(1, 1, position, board, piece).limit(1),
        _getHorizontalMotion(-1, -1, position, board, piece).limit(1),
        _getHorizontalMotion(-1, 1, position, board, piece).limit(1),
        _getHorizontalMotion(-1, 1, position, board, piece).limit(1))
        .flatMap(s -> s);
  }

  private static Stream<ChessPosition> _getHorizontalMotion(int dx, int dy, ChessPosition position, ChessBoard board,
      ChessPiece piece) {
    return Stream.iterate(0, n -> n + 1)
        .map(n -> position.add(dx, dy))
        .filter(p -> p.isInRange())
        .takeWhile(p -> (board.getPiece(p) == null))
        .takeWhile(p -> (board.getPiece(p).getTeamColor() != piece.pieceColor()));
  }

  public static Stream<ChessPosition> queenMoves(ChessPosition position, ChessBoard board, ChessPiece piece) {
    return Stream.of(
        _getHorizontalMotion(1, 0, position, board, piece),
        _getHorizontalMotion(-1, 0, position, board, piece),
        _getHorizontalMotion(0, 1, position, board, piece),
        _getHorizontalMotion(0, -1, position, board, piece),
        _getHorizontalMotion(1, 1, position, board, piece),
        _getHorizontalMotion(-1, -1, position, board, piece),
        _getHorizontalMotion(-1, 1, position, board, piece),
        _getHorizontalMotion(-1, 1, position, board, piece))
        .flatMap(s -> s);
  }

  public static Stream<ChessPosition> knightMoves(ChessPosition position, ChessBoard board, ChessPiece piece) {
    return Stream.of(
        position.add(2, 1),
        position.add(2, -1),
        position.add(1, 2),
        position.add(1, -2),
        position.add(-2, -1),
        position.add(-2, 1))
        .filter(p -> p.isInRange() && board.getPiece(p) == null
            || board.getPiece(p).getTeamColor() != piece.getTeamColor());
  }

  public static Stream<ChessPosition> rookMoves(ChessPosition position, ChessBoard board, ChessPiece piece) {
    return Stream.of(
        _getHorizontalMotion(1, 0, position, board, piece),
        _getHorizontalMotion(-1, 0, position, board, piece),
        _getHorizontalMotion(0, 1, position, board, piece),
        _getHorizontalMotion(0, -1, position, board, piece))
        .flatMap(s -> s);
  }

  public static Stream<ChessPosition> pawnMoves(ChessPosition position, ChessBoard board, ChessPiece piece) {
    var color = piece.getTeamColor();
    return (switch (color) {
      case WHITE -> detailedPawnMoves(position, board, 1, 2);
      case BLACK -> detailedPawnMoves(position, board, -1, 7);
    })
        .filter(p -> (p.isInRange() && board.getPiece(p).getTeamColor() != color));
  }

  private static Stream<ChessPosition> detailedPawnMoves(ChessPosition position, ChessBoard board, int moveBy,
      int row2) {
    var canMove2 = position.getRow() == row2;
    var nextPos = position.add(0, moveBy);
    var nextPiece = board.getPiece(nextPos);
    var enemyInFront = nextPiece != null && nextPiece.getTeamColor() == TeamColor.BLACK;
    if (enemyInFront) {
      return Stream.of(position.add(1, moveBy), position.add(-1, moveBy));
    } else if (canMove2) {
      return Stream.of(nextPos, nextPos.add(0, moveBy));
    } else {
      return Stream.of(nextPos);
    }
  }

  public static Stream<ChessPosition> bishopMoves(ChessPosition position, ChessBoard board, ChessPiece piece) {
    return Stream.of(
        _getHorizontalMotion(1, 1, position, board, piece),
        _getHorizontalMotion(1, -1, position, board, piece),
        _getHorizontalMotion(-1, 1, position, board, piece),
        _getHorizontalMotion(-1, -1, position, board, piece))
        .flatMap(s -> s);
  }
}
