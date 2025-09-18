package chess;

import java.util.ArrayList;
import java.util.stream.Stream;

import chess.ChessGame.TeamColor;
import chess.ChessPiece.PieceType;

final class ChessRules {

  public static Stream<ChessMove> kingMoves(ChessPosition position, ChessBoard board, ChessPiece piece) {
    return Stream.of(
        _linearMotion(1, 0, position, board, piece).limit(1),
        _linearMotion(-1, 0, position, board, piece).limit(1),
        _linearMotion(0, 1, position, board, piece).limit(1),
        _linearMotion(0, -1, position, board, piece).limit(1),
        _linearMotion(1, 1, position, board, piece).limit(1),
        _linearMotion(-1, -1, position, board, piece).limit(1),
        _linearMotion(1, -1, position, board, piece).limit(1),
        _linearMotion(-1, 1, position, board, piece).limit(1))
        .flatMap(s -> s)
        .map(p -> new ChessMove(position, p));
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

  public static Stream<ChessMove> queenMoves(ChessPosition position, ChessBoard board, ChessPiece piece) {
    return Stream.of(
        _linearMotion(1, 0, position, board, piece),
        _linearMotion(-1, 0, position, board, piece),
        _linearMotion(0, 1, position, board, piece),
        _linearMotion(0, -1, position, board, piece),
        _linearMotion(1, 1, position, board, piece),
        _linearMotion(-1, -1, position, board, piece),
        _linearMotion(1, -1, position, board, piece),
        _linearMotion(-1, 1, position, board, piece))
        .flatMap(s -> s)
        .map(p -> new ChessMove(position, p));
  }

  public static Stream<ChessMove> knightMoves(ChessPosition position, ChessBoard board, ChessPiece piece) {
    return Stream.of(
        position.add(2, 1),
        position.add(2, -1),
        position.add(1, 2),
        position.add(1, -2),
        position.add(-1, -2),
        position.add(-1, 2),
        position.add(-2, -1),
        position.add(-2, 1))
        .filter(p -> {
          if (!p.isInRange()) {
            return false;
          }
          if (board.getPiece(p) == null) {
            return true;
          }
          if (board.getPiece(p).getTeamColor() == piece.getTeamColor()) {
            return false;
          }
          if (board.getPiece(p).getPieceType() == PieceType.KING) {
            return false;
          }
          return true;
        })
        .map(p -> new ChessMove(position, p));
  }

  public static Stream<ChessMove> rookMoves(ChessPosition position, ChessBoard board, ChessPiece piece) {
    return Stream.of(
        _linearMotion(1, 0, position, board, piece),
        _linearMotion(-1, 0, position, board, piece),
        _linearMotion(0, 1, position, board, piece),
        _linearMotion(0, -1, position, board, piece))
        .flatMap(s -> s)
        .map(p -> new ChessMove(position, p));
  }

  public static Stream<ChessMove> pawnMoves(ChessPosition position, ChessBoard board, ChessPiece piece) {
    var color = piece.getTeamColor();
    return (switch (color) {
      case WHITE -> detailedPawnMoves(position, board, 1, 2, 8, TeamColor.WHITE);
      case BLACK -> detailedPawnMoves(position, board, -1, 7, 1, TeamColor.BLACK);
    });
  }

  private static Stream<ChessMove> detailedPawnMoves(ChessPosition position, ChessBoard board, int moveBy,
      int row2, int row8, TeamColor color) {
    var moves = new ArrayList<ChessMove>();
    if (position == null || board == null)
      return Stream.of();
    var nextPos = position.add(0, moveBy);
    var nextPiece = board.getPiece(nextPos);
    var nextNextPos = nextPos.add(0, moveBy);
    var nextNextPiece = board.getPiece(nextNextPos);
    var canMove2 = position.getRow() == row2 && (nextNextPiece == null);
    var attack1 = position.add(-1, moveBy);
    var pieceToAttack1 = board.getPiece(attack1);
    var attack2 = position.add(1, moveBy);
    var pieceToAttack2 = board.getPiece(attack2);
    var canAttack1 = pieceToAttack1 != null
        && pieceToAttack1.getTeamColor() != color
        && pieceToAttack1.getPieceType() != PieceType.KING;
    var canAttack2 = pieceToAttack2 != null
        && pieceToAttack2.getTeamColor() != color
        && pieceToAttack2.getPieceType() != PieceType.KING;
    var blocked = nextPiece != null;
    var isAtEnd = position.getRow() == (row8 - moveBy);
    if (isAtEnd) {
      for (var val : PieceType.values()) {
        if (val == PieceType.PAWN || val == PieceType.KING)
          continue;
        if (canAttack1) {
          moves.add(new ChessMove(position, attack1, val));
        }
        if (canAttack2) {
          moves.add(new ChessMove(position, attack2, val));
        }
        if (!blocked) {
          moves.add(new ChessMove(position, nextPos, val));
        }
      }
    } else {
      if (!blocked) {
        if (canMove2) {
          moves.add(new ChessMove(position, nextNextPos));
        }
        moves.add(new ChessMove(position, nextPos));
      }
      if (canAttack1) {
        moves.add(new ChessMove(position, attack1));
      }
      if (canAttack2) {
        moves.add(new ChessMove(position, attack2));
      }
    }
    return moves.stream().filter(x -> x.getEndPosition().isInRange());
  }

  public static Stream<ChessMove> bishopMoves(ChessPosition position, ChessBoard board, ChessPiece piece) {
    return Stream.of(
        _linearMotion(1, 1, position, board, piece),
        _linearMotion(1, -1, position, board, piece),
        _linearMotion(-1, 1, position, board, piece),
        _linearMotion(-1, -1, position, board, piece))
        .flatMap(s -> s)
        .map(p -> new ChessMove(position, p));
  }
}
