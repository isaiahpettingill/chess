package chess;

import java.util.ArrayList;

public final class ChessHistory {
    private final ArrayList<HistoryEntry> entries;
    public record HistoryEntry(String board, ChessMove move){
        public String toString(){
            return board + "\n" + move.toString();
        }
    }

    public ChessHistory(){
        entries = new ArrayList<>();
    }

    public void saveMove(ChessBoard board, ChessMove move){
        var boardString = board.toString();
        entries.add(new HistoryEntry(boardString, move));
    }   

    public int getHistoryLength(){
        return entries.size();
    }

    public String toString(){
        final var output = new StringBuilder();
        for(var entry : entries){
            output.append(entry.toString());
            output.append("\n\n");
        }   
        return output.toString();
    }
}
