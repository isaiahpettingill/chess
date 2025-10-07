package chess;

import java.util.ArrayList;
import java.util.stream.Stream;

import chess.ChessPiece.PieceType;

public final class ChessRules {
    private ChessRules(){}

    public static Stream<ChessMove> getPieceMovesRaw(ChessPiece.PieceType type, ChessGame.TeamColor color, ChessBoard board, ChessPosition pos) {
        if (type == null || color == null || board == null || pos == null) return Stream.of();
        return switch (type) {
            case KING -> _getKingMoves(color, board, pos);
            case PAWN -> _getPawnMoves(color, board, pos);
            case ROOK -> _getRookMoves(color, board, pos);
            case QUEEN -> _getQueenMoves(color, board, pos);
            case BISHOP -> _getBishopMoves(color, board, pos);
            case KNIGHT -> _getKnightMoves(color, board, pos);
        };
    }

    public static Stream<ChessMove> getPieceMoves(ChessPiece.PieceType type, ChessGame.TeamColor color, ChessBoard board, ChessPosition pos){
        return getPieceMovesRaw(type, color, board, pos).filter(move -> _isNotKing(board, move));
    }

    private static boolean _isNotKing(ChessBoard board, ChessMove move){
        final var piece = board.getPiece(move.getEndPosition());
        if (piece == null) return true;
        return piece.getPieceType() != PieceType.KING;
    }

    private static boolean _isAttackable(ChessPosition pos, ChessGame.TeamColor color, ChessBoard board) {
        if (!pos.isInRange()) {
            return false;
        }
        final var piece = board.getPiece(pos);
        if (piece == null) {
            return true;
        }
        return piece.getTeamColor() != color;
    }

    private static Stream<ChessMove> _getKnightMoves(ChessGame.TeamColor color, ChessBoard board, ChessPosition pos) {
        return Stream.of(
                        pos.add(2, 1),
                        pos.add(2, -1),
                        pos.add(-2, 1),
                        pos.add(-2, -1),
                        pos.add(1, -2),
                        pos.add(1, 2),
                        pos.add(-1, -2),
                        pos.add(-1, 2)
                )
                .filter(x -> _isAttackable(x, color, board))
                .map(x -> new ChessMove(pos, x));
    }

    private static Stream<ChessMove> _getBishopMoves(ChessGame.TeamColor color, ChessBoard board, ChessPosition pos) {
        return Stream.of(
                        _motion(pos, 1, 1, color, board),
                        _motion(pos, -1, -1, color, board),
                        _motion(pos, 1, -1, color, board),
                        _motion(pos, -1, 1, color, board)
                )
                .flatMap(s -> s)
                .map(x -> new ChessMove(pos, x));
    }

    private static Stream<ChessMove> _getQueenMoves(ChessGame.TeamColor color, ChessBoard board, ChessPosition pos) {
        return Stream.of(
                        _motion(pos, 1, 0, color, board),
                        _motion(pos, 1, 1, color, board),
                        _motion(pos, 1, -1, color, board),
                        _motion(pos, -1, 0, color, board),
                        _motion(pos, -1, -1, color, board),
                        _motion(pos, -1, 1, color, board),
                        _motion(pos, 0, 1, color, board),
                        _motion(pos, 0, -1, color, board)
                )
                .flatMap(s -> s)
                .map(x -> new ChessMove(pos, x));
    }

    private static Stream<ChessMove> _getRookMoves(ChessGame.TeamColor color, ChessBoard board, ChessPosition pos) {
        return Stream.of(
                        _motion(pos, 1, 0, color, board),
                        _motion(pos, -1, 0, color, board),
                        _motion(pos, 0, -1, color, board),
                        _motion(pos, 0, 1, color, board)
                )
                .flatMap(s -> s)
                .map(x -> new ChessMove(pos, x));
    }

    private static Stream<ChessMove> _getPawnMoves(ChessGame.TeamColor color, ChessBoard board, ChessPosition pos) {
        final int forward = color == ChessGame.TeamColor.BLACK ? -1 : 1;
        final int start = color == ChessGame.TeamColor.BLACK ? 7 : 2;
        final int end = color == ChessGame.TeamColor.BLACK ? 1 : 8;
        final var canMoveTwo = pos.getRow() == start;
        final var isAtEnd = pos.getRow() == end - forward;
        final var moves = new ArrayList<ChessMove>();
        final var nextPos = pos.add(0, forward);
        final var nextNextPos = pos.add(0, forward * 2);
        final var attackPos1 = pos.add(1, forward);
        final var attackPos2 = pos.add(-1, forward);
        final var canAttack1 = board.getPiece(attackPos1) != null && _isAttackable(attackPos1, color, board);
        final var canAttack2 = board.getPiece(attackPos2) != null && _isAttackable(attackPos2, color, board);
        final var blocked = board.getPiece(nextPos) != null;
        final var nextBlocked = blocked || board.getPiece(nextNextPos) != null;
        if (isAtEnd) {
            for (var type : ChessPiece.PieceType.values()){
                if (type == ChessPiece.PieceType.KING || type == ChessPiece.PieceType.PAWN) continue;
                if (!blocked) moves.add(new ChessMove(pos, nextPos, type));
                if (canAttack1){
                    moves.add(new ChessMove(pos, attackPos1, type));
                }
                if (canAttack2){
                    moves.add(new ChessMove(pos, attackPos2, type));
                }
            }
        }
        else {
            if (!blocked) moves.add(new ChessMove(pos, nextPos));
            if (canMoveTwo && !nextBlocked){
                moves.add(new ChessMove(pos, nextNextPos));
            }
            if (canAttack1){
                moves.add(new ChessMove(pos, attackPos1));
            }
            if (canAttack2){
                moves.add(new ChessMove(pos, attackPos2));
            }
        }
        return moves.stream().filter(x -> x.getEndPosition().isInRange());
    }


    private static Stream<ChessPosition> _motion(ChessPosition start, int dx, int dy, ChessGame.TeamColor color, ChessBoard board) {
        final var positions = new ArrayList<ChessPosition>();
        var pos = start.add(dx, dy);
        while (_isAttackable(pos, color, board)) {
            positions.add(pos);
            if (board.getPiece(pos) != null) {
                break;
            }
            pos = pos.add(dx, dy);
        }
        return positions.stream();
    }

    private static Stream<ChessMove> _getKingMoves(ChessGame.TeamColor color, ChessBoard board, ChessPosition pos) {
        return Stream.of(
                        pos.addX(1),
                        pos.addX(-1),
                        pos.addY(1),
                        pos.addY(-1),
                        pos.add(1, 1),
                        pos.add(1, -1),
                        pos.add(-1, 1),
                        pos.add(-1, -1))
                .filter(x -> _isAttackable(x, color, board))
                .map(x -> new ChessMove(pos, x));
    }


}
