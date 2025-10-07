package chess;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;

public final class ChessHistory {
    private final ArrayList<HistoryEntry> _entries;
    public record HistoryEntry(String board, ChessMove move){
        public String toString(){
            return board + "\n" + move.toString();
        }
    }

    public ChessHistory(){
        _entries = new ArrayList<>();
    }

    public void saveMove(ChessBoard board, ChessMove move){
        var boardString = board.toString();
        _entries.add(new HistoryEntry(boardString, move));
    }   

    public int getHistoryLength(){
        return _entries.size();
    }

    public Optional<HistoryEntry> goBackNEntries(int n){
        if (n > _entries.size()) return Optional.empty();

        return Optional.of(_entries.get(_entries.size() - n - 1));
    }

    public String toString(){
        final var output = new StringBuilder();
        for(var entry : _entries){
            output.append(entry.toString());
            output.append("\n\n");
        }   
        return output.toString();
    }

    public Stream<String> replayGame(){
        return _entries.stream().map(x -> x.toString());
    }
}
