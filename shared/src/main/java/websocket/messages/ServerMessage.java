package websocket.messages;

import java.util.Objects;

import chess.ChessGame;

/**
 * Represents a Message the server can send through a WebSocket
 * <p>
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class ServerMessage {
    private final ServerMessageType serverMessageType;

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    public static class LoadGameMessage extends ServerMessage {
        private final ChessGame game;
        public LoadGameMessage(ChessGame game) {
            super(ServerMessageType.LOAD_GAME);
            this.game = game;
        }

        public ChessGame game(){ return game; }
    }

    public static class ErrorMessage extends ServerMessage {
        private final String errorMessage;
        public ErrorMessage(String err) {
            super(ServerMessageType.ERROR);
            this.errorMessage = err;
        }

        public String errorMessage(){ return errorMessage; }
    }

    public static class NotificationMessage extends ServerMessage {
        private final String message;
        public NotificationMessage(String msg) {
            super(ServerMessageType.NOTIFICATION);
            message = msg;
        }

        public String message(){ return message; }
    }

    public ServerMessage(ServerMessageType type) {
        this.serverMessageType = type;
    }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServerMessage that)) {
            return false;
        }
        return getServerMessageType() == that.getServerMessageType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType());
    }
}
