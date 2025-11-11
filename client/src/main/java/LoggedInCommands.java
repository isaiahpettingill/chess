import java.io.Console;

import client.ServerFacade;
import dto.CreateGamePayload;
import dto.JoinGamePayload;
import static ui.EscapeSequences.*;

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

            var gameIdInteger = Integer.parseInt(gameId);

            CONSOLE.printf("websocket not yet implemented! Will observe game: %d", gameIdInteger);

        } catch (NumberFormatException ex) {
            CONSOLE.printf("Bro, the ID needs to be an integer dang it.");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void joinGame() {
        try {
            final var gameId = CONSOLE.readLine("[Game id]: ");
            final var color = CONSOLE.readLine("[BLACK/WHITE]: ");

            if (!"WHITE".equals(color.toUpperCase()) && !"BLACK".equals(color.toUpperCase())) {
                CONSOLE.printf(SET_TEXT_COLOR_RED + "\tColor must be BLACK or WHITE\n"
                        + RESET_TEXT_COLOR);
                return;
            }

            var gameIdInteger = Integer.parseInt(gameId);

            backend.joinGame(new JoinGamePayload(color.toUpperCase(), gameIdInteger));
            CONSOLE.printf("success");

        } catch (NumberFormatException ex) {
            CONSOLE.printf(SET_TEXT_COLOR_RED + "Bro, the ID needs to be an integer dang it." + RESET_TEXT_COLOR);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void createGame() {
        try {
            final var gameName = CONSOLE.readLine("[Game name]: ");
            final var game = backend.createGame(new CreateGamePayload(gameName));
            var id = game.body().gameID();
            CONSOLE.printf("Game created. (ID: %d)\n", id);

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
                    CONSOLE.printf("Game: %s (id %d) [%s vs %s]\n", game.gameName(), game.gameID(),
                            game.whiteUsername(),
                            game.blackUsername());
                    CONSOLE.printf(RESET_TEXT_COLOR);
                }
            }

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
