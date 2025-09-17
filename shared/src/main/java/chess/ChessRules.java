package chess;

import java.util.ArrayList;
import java.util.stream.Stream;

import chess.ChessGame.TeamColor;
import chess.ChessPiece.PieceType;

final class ChessRules {

  public static Stream<ChessPosition> kingMoves(ChessPosition position, ChessBoard board, ChessPiece piece) {
    return Stream.of(
        _linearMotion(1, 0, position, board, piece).limit(1),
        _linearMotion(-1, 0, position, board, piece).limit(1),
        _linearMotion(0, 1, position, board, piece).limit(1),
        _linearMotion(0, -1, position, board, piece).limit(1),
        _linearMotion(1, 1, position, board, piece).limit(1),
        _linearMotion(-1, -1, position, board, piece).limit(1),
        _linearMotion(1, -1, position, board, piece).limit(1),
        _linearMotion(-1, 1, position, board, piece).limit(1))
        .flatMap(s -> s);
  }

  private static Stream<ChessPosition> _linearMotion(int dx, int dy, ChessPosition position, ChessBoard board,
      ChessPiece piece) {
    var positions = new ArrayList<ChessPosition>();
    var p = position.add(dx, dy);
    ;
    while (p.isInRange()) {
      if (board.getPiece(p) == null) {
        positions.add(p);
      } else if (board.getPiece(p).getTeamColor() != piece.getTeamColor()) {
        if (board.getPiece(p).getPieceType() != PieceType.KING) {
          positions.add(p);
        }
        break;
      } else {
        break;
      }
      p = p.add(dx, dy);
    }
    return positions.stream();
  }

  public static Stream<ChessPosition> queenMoves(ChessPosition position, ChessBoard board, ChessPiece piece) {
    return Stream.of(
        _linearMotion(1, 0, position, board, piece),
        _linearMotion(-1, 0, position, board, piece),
        _linearMotion(0, 1, position, board, piece),
        _linearMotion(0, -1, position, board, piece),
        _linearMotion(1, 1, position, board, piece),
        _linearMotion(-1, -1, position, board, piece),
        _linearMotion(1, -1, position, board, piece),
        _linearMotion(-1, 1, position, board, piece))
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
        .filter(p -> {
          if (!p.isInRange()){
            return false;
          }
          if (board.getPiece(p) == null) {
            return true;
          }
          if (board.getPiece(p).getTeamColor() == piece.getTeamColor()){
            return false;
          }
          if (board.getPiece(p).getPieceType() == PieceType.KING){
            return false;
          }
          return true;
        });
  }

  public static Stream<ChessPosition> rookMoves(ChessPosition position, ChessBoard board, ChessPiece piece) {
    return Stream.of(
        _linearMotion(1, 0, position, board, piece),
        _linearMotion(-1, 0, position, board, piece),
        _linearMotion(0, 1, position, board, piece),
        _linearMotion(0, -1, position, board, piece))
        .flatMap(s -> s);
  }

  public static Stream<ChessPosition> pawnMoves(ChessPosition position, ChessBoard board, ChessPiece piece) {
    var color = piece.getTeamColor();
    return (switch (color) {
      case WHITE -> detailedPawnMoves(position, board, 1, 2, TeamColor.WHITE);
      case BLACK -> detailedPawnMoves(position, board, -1, 7, TeamColor.BLACK);
    })
        .filter(p -> (p.isInRange() && board.getPiece(p) == null));
  }

  private static Stream<ChessPosition> detailedPawnMoves(ChessPosition position, ChessBoard board, int moveBy,
      int row2, TeamColor color) {
    if (position == null || board == null)
      return Stream.of();
    var canMove2 = position.getRow() == row2;
    var nextPos = position.add(0, moveBy);
    var nextPiece = board.getPiece(nextPos);
    var enemyInFront = nextPiece != null && nextPiece.getTeamColor() != color;
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
        _linearMotion(1, 1, position, board, piece),
        _linearMotion(1, -1, position, board, piece),
        _linearMotion(-1, 1, position, board, piece),
        _linearMotion(-1, -1, position, board, piece))
        .flatMap(s -> s);
  }
}
