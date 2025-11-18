package chess;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public final record ChessPosition(int row, int col) {
    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    public ChessPosition add(int dx, int dy){
        return new ChessPosition(row + dy, col + dx);
    }

    public ChessPosition addX(int dx){
        return new ChessPosition(row, col + dx);
    }

    public ChessPosition addY(int dy){
        return new ChessPosition(row + dy, col);
    }

    public boolean isInRange(){
        return row > 0 && row < 9 && col > 0 && col < 9; // board is 9x9 for 1-indexing, but 0 is invalid
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return col;
    }

    private static final String letterMap = "abcdefgh";

    public String toString(){
        return "(" + letterMap.charAt(col) + ":" + row + ")";
    }
}
