package passoff.chess;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessGame.TeamColor;
import chess.ChessPiece.PieceType;

public final class PrettyPrintTests {
    
    @Test()
    public void testPrettyPrint(){
        final var chess = new ChessGame();
        assertNotNull(chess.toPrettyString(false));
        assertNotNull(chess.toPrettyString(true));      
    }

    @Test()
    public void testRotation(){
        var board = new ChessPiece[9][9];
        var piece = new ChessPiece(TeamColor.WHITE, PieceType.QUEEN);
        board[1][1] = piece;
        ChessGame.rotateBoard180Degrees(board);
        assertEquals(board[8][8], piece);
    }
}
