import static ui.EscapeSequences.RESET_BG_COLOR;
import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_BG_COLOR_WHITE;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLACK;
import static ui.EscapeSequences.SET_TEXT_COLOR_GREEN;
import static ui.EscapeSequences.SET_TEXT_COLOR_RED;

import java.io.Console;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
    private final List<dto.ListGamesResponse.ListGamesGame> currentGames = new ArrayList<>();

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
            CONSOLE.printf(ex.getMessage());
        }
    }

    private void observeGame() {
        try {
            final var gameIdStr = CONSOLE.readLine("[Game number]: ").trim();
            if (gameIdStr.isEmpty()) {
                CONSOLE.printf(SET_TEXT_COLOR_RED + "Bro, you gotta enter a game number, don't leave it blank!" + RESET_TEXT_COLOR);
                return;
            }
            int gameNum = Integer.parseInt(gameIdStr);
            if (gameNum <= 0 || gameNum > currentGames.size()) {
                CONSOLE.printf(SET_TEXT_COLOR_RED + "Bro, game number outta range, list the games first!" + RESET_TEXT_COLOR);
                return;
            }
            int decodedId = currentGames.get(gameNum - 1).gameID();
            var game = backend.getGame(decodedId);
            if (game.status() != 200) {
                CONSOLE.printf(SET_TEXT_COLOR_RED + "Yo, that game ain't real, bro. Check the number." + RESET_TEXT_COLOR);
                return;
            }
            CONSOLE.printf(SET_TEXT_COLOR_GREEN + "success" + RESET_TEXT_COLOR);
            var chessGame = game.body().game();
            var asRealObject = new Gson().fromJson(chessGame, ChessGame.class);
            CONSOLE.printf(asRealObject.toPrettyString(true) + "\n");
        } catch (NumberFormatException ex) {
            CONSOLE.printf(SET_TEXT_COLOR_RED + "Bro, the game number needs to be an integer dang it." + RESET_TEXT_COLOR);
        } catch (Exception ex) {
            CONSOLE.printf(SET_TEXT_COLOR_RED + ex.getMessage() + RESET_TEXT_COLOR);
        }
    }

    private void joinGame() {
        try {
            final var gameIdStr = CONSOLE.readLine("[Game number]: ").trim();
            if (gameIdStr.isEmpty()) {
                CONSOLE.printf(SET_TEXT_COLOR_RED + "Bro, you gotta enter a game number, don't leave it blank!" + RESET_TEXT_COLOR);
                return;
            }
            final var colorStr = CONSOLE.readLine("[" + SET_BG_COLOR_WHITE
                    + SET_TEXT_COLOR_BLACK + "BLACK"
                    + RESET_BG_COLOR + RESET_TEXT_COLOR + "/" + "WHITE" + "]: ").trim();
            if (colorStr.isEmpty()) {
                CONSOLE.printf(SET_TEXT_COLOR_RED + "Dude, pick a color, BLACK or WHITE, bro!" + RESET_TEXT_COLOR);
                return;
            }
            String color = colorStr.toUpperCase();
            if (!"WHITE".equals(color) && !"BLACK".equals(color)) {
                CONSOLE.printf(SET_TEXT_COLOR_RED + "Dude, color's gotta be BLACK or WHITE, bro." + RESET_TEXT_COLOR);
                return;
            }
            int gameNum = Integer.parseInt(gameIdStr);
            if (gameNum <= 0 || gameNum > currentGames.size()) {
                CONSOLE.printf(SET_TEXT_COLOR_RED + "Bro, game number outta range, list the games first!" + RESET_TEXT_COLOR);
                return;
            }
            int decodedId = currentGames.get(gameNum - 1).gameID();
            var game = backend.getGame(decodedId);
            if (game.status() != 200) {
                CONSOLE.printf(SET_TEXT_COLOR_RED + "Game not found, bro. Try again." + RESET_TEXT_COLOR);
                return;
            }
            var result = backend.joinGame(new JoinGamePayload(color, decodedId));
            if (result.status() != 200) {
                if (result.status() == 403) {
                    CONSOLE.printf(SET_TEXT_COLOR_RED + "Bro, that spot's already taken!" + RESET_TEXT_COLOR);
                } else if (result.status() == 401) {
                    CONSOLE.printf(SET_TEXT_COLOR_RED + "Unauthorized, bro. You sure you're logged in?" + RESET_TEXT_COLOR);
                } else if (result.status() == 500) {
                    CONSOLE.printf(SET_TEXT_COLOR_RED + "Server error, bro. Try again later." + RESET_TEXT_COLOR);
                } else {
                    CONSOLE.printf(SET_TEXT_COLOR_RED + "Couldn't join, status " + result.status() + RESET_TEXT_COLOR);
                }
                return;
            }
            CONSOLE.printf(SET_TEXT_COLOR_GREEN + "success" + RESET_TEXT_COLOR);
            var chessGame = game.body().game();
            var asRealObject = new Gson().fromJson(chessGame, ChessGame.class);
            CONSOLE.printf(asRealObject.toPrettyString(color.equals("WHITE")) + "\n");
        } catch (NumberFormatException ex) {
            CONSOLE.printf(SET_TEXT_COLOR_RED + "Bro, the game number needs to be an integer dang it." + RESET_TEXT_COLOR);
        } catch (Exception ex) {
            CONSOLE.printf(SET_TEXT_COLOR_RED + ex.getMessage() + RESET_TEXT_COLOR);
        }
    }

    private void createGame() {
        try {
            final var gameName = CONSOLE.readLine("[Game name]: ").trim();
            if (gameName.isEmpty()) {
                CONSOLE.printf(SET_TEXT_COLOR_RED + "Bro, game name can't be empty, give it a name!" + RESET_TEXT_COLOR);
                return;
            }
            var result = backend.createGame(new CreateGamePayload(gameName));
            if (result.status() != 200) {
                if (result.status() == 401) {
                    CONSOLE.printf(SET_TEXT_COLOR_RED + "Unauthorized, bro. You sure you're logged in?" + RESET_TEXT_COLOR);
                } else if (result.status() == 500) {
                    CONSOLE.printf(SET_TEXT_COLOR_RED + "Server error, bro. Try again later." + RESET_TEXT_COLOR);
                } else {
                    CONSOLE.printf(SET_TEXT_COLOR_RED + "Couldn't create game, status " + result.status() + RESET_TEXT_COLOR);
                }
                return;
            }
            CONSOLE.printf(SET_TEXT_COLOR_GREEN + "Game created.\n" + RESET_TEXT_COLOR);
        } catch (Exception ex) {
            CONSOLE.printf(SET_TEXT_COLOR_RED + ex.getMessage() + RESET_TEXT_COLOR);
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
                currentGames.clear();
                currentGames.addAll(allGames);
                currentGames.sort(Comparator.comparing(dto.ListGamesResponse.ListGamesGame::gameName));
                CONSOLE.printf(SET_TEXT_COLOR_GREEN);
                if (currentGames.size() == 0) {
                    CONSOLE.printf("No games! Create one to play.\n");
                    return;
                }
                CONSOLE.printf("\n[GAMES]\n");
                for (int i = 0; i < currentGames.size(); i++) {
                    final var game = currentGames.get(i);
                    CONSOLE.printf((i+1) + ". Game: %s [%s vs %s]\n", game.gameName(),
                            game.whiteUsername(),
                            game.blackUsername());
                }
                CONSOLE.printf(RESET_TEXT_COLOR);
            }

        } catch (Exception ex) {
            CONSOLE.printf(ex.getMessage());
        }
    }
}
