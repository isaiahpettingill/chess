import static ui.EscapeSequences.RESET_BG_COLOR;
import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_BG_COLOR_WHITE;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLACK;
import static ui.EscapeSequences.SET_TEXT_COLOR_GREEN;
import static ui.EscapeSequences.SET_TEXT_COLOR_RED;

import java.io.Console;

import com.google.gson.Gson;

import chess.ChessGame;
import client.ServerFacade;
import dto.CreateGamePayload;
import dto.JoinGamePayload;

final class LoggedInCommands {
    private final ServerFacade backend;
    private static final Console CONSOLE = System.console();
    private final BooleanSetter setShouldContinue;
    private final BooleanSetter setLoggedIn;

    public LoggedInCommands(ServerFacade backend, BooleanSetter setShouldContinue, BooleanSetter setLoggedIn) {
        this.backend = backend;
        this.setShouldContinue = setShouldContinue;
        this.setLoggedIn = setLoggedIn;
    }

    public interface BooleanSetter {
        void set(boolean val);
    }

    public void handleLoggedIn(int input) {
        switch (input) {
            case 0:
                help();
                break;
            case 1:
                listGames();
                break;
            case 2:
                createGame();
                break;
            case 3:
                joinGame();
                break;
            case 4:
                observeGame();
                break;
            case 5:
                logout();
                break;
            case 6:
                setShouldContinue.set(false);
                break;
            default:
                return;
        }
        CONSOLE.printf(RESET_TEXT_COLOR);
    }

    private void help() {
        CONSOLE.printf(SET_TEXT_COLOR_GREEN);
        CONSOLE.printf("\tEnter 0 to see this message\n");
        CONSOLE.printf("\tEnter 1 to list all available games\n");
        CONSOLE.printf("\tEnter 2 to create a new game\n");
        CONSOLE.printf("\tEnter 3 to play a game over the network\n");
        CONSOLE.printf("\tEnter 4 to observe a game over the network\n");
        CONSOLE.printf("\tEnter 5 to sign out\n");
        CONSOLE.printf("\tEnter 6 to close the application and clear credentials\n");
        CONSOLE.printf(RESET_TEXT_COLOR);
    }

    private void logout() {
        try {
            backend.logout();
            setLoggedIn.set(false);
            setShouldContinue.set(true);
            CONSOLE.printf("Logged out!\n");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void observeGame() {
        try {
            final var gameId = CONSOLE.readLine("[Game id]: ");

            var game = backend.getGame(gameId);
            if (game.status() != 200) {
                CONSOLE.printf("FAILED TO FIND GAME");
                return;
            }

            CONSOLE.printf(SET_TEXT_COLOR_GREEN + "success" + RESET_TEXT_COLOR);

            var chessGame = game.body().game();
            var asRealObject = new Gson().fromJson(chessGame, ChessGame.class);
            CONSOLE.printf(asRealObject.prettyPrint(true) + "\n");

        } catch (NumberFormatException ex) {
            CONSOLE.printf(SET_TEXT_COLOR_RED + "Bro, the ID needs to be an integer dang it."
                    + RESET_TEXT_COLOR);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void joinGame() {
        try {
            final var gameId = CONSOLE.readLine("[Game id]: ");
            final var color = CONSOLE.readLine("[" + SET_BG_COLOR_WHITE
                    + SET_TEXT_COLOR_BLACK + "BLACK"
                    + RESET_BG_COLOR + RESET_TEXT_COLOR + "/" + "WHITE" + "]: ");

            if (!"WHITE".equals(color.toUpperCase()) && !"BLACK".equals(color.toUpperCase())) {
                CONSOLE.printf(SET_TEXT_COLOR_RED + "\tColor must be BLACK or WHITE\n"
                        + RESET_TEXT_COLOR);
                return;
            }

            var game = backend.getGame(gameId);
            if (game.status() != 200) {
                CONSOLE.printf("FAILED TO FIND GAME");
                return;
            }
            var result = backend.joinGame(new JoinGamePayload(color.toUpperCase(), gameId));
            if (result.status() != 200) {
                CONSOLE.printf("FAILED TO JOIN GAME");
                return;
            }

            CONSOLE.printf(SET_TEXT_COLOR_GREEN + "success" + RESET_TEXT_COLOR);

            var chessGame = game.body().game();
            var asRealObject = new Gson().fromJson(chessGame, ChessGame.class);
            CONSOLE.printf(asRealObject.prettyPrint(color.toUpperCase().equals("WHITE")) + "\n");
        } catch (NumberFormatException ex) {
            CONSOLE.printf(SET_TEXT_COLOR_RED + "Invalid ID" + RESET_TEXT_COLOR);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void createGame() {
        try {
            final var gameName = CONSOLE.readLine("[Game name]: ");
            final var game = backend.createGame(new CreateGamePayload(gameName));
            var id = game.body().gameID();
            CONSOLE.printf(SET_TEXT_COLOR_GREEN + "Game created. (ID: %s)\n" + RESET_TEXT_COLOR, id);

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void listGames() {
        try {
            var games = backend.listGames();

            if (games.status() != 200) {
                CONSOLE.printf(SET_TEXT_COLOR_RED + "Error. Status %d\n"
                        + RESET_TEXT_COLOR,
                        games.status());
            } else {
                final var allGames = games.body().games();
                CONSOLE.printf(SET_TEXT_COLOR_GREEN);
                if (allGames.size() == 0) {
                    CONSOLE.printf("No games! Create one to play.\n");
                    return;
                }
                CONSOLE.printf("\n[GAMES]\n");
                for (final var game : allGames) {
                    CONSOLE.printf("Game: %s [%s vs %s]\n", game.gameName(),
                            game.whiteUsername(),
                            game.blackUsername());
                }
                CONSOLE.printf(RESET_TEXT_COLOR);
            }

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
