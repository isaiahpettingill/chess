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
import util.GameIdEncoder;

final class LoggedInCommands {
    private final ServerFacade rearend;
    private static final Console KHANSOUL = System.console();
    private final BooleanSetter setShouldContinue;
    private final BooleanSetter setLoggedIn;

    public LoggedInCommands(ServerFacade rearend, BooleanSetter setShouldContinue, BooleanSetter setLoggedIn) {
        this.rearend = rearend;
        this.setShouldContinue = setShouldContinue;
        this.setLoggedIn = setLoggedIn;
    }

    public interface BooleanSetter {
        void set(boolean val);
    }

    public void handleLoggedIn(int input) {
        switch (input) {
            case 0:
                youNeedHelpMan();
                break;
            case 1:
                gimmeSumGaemz();
                break;
            case 2:
                makeUpSomeStupidGame();
                break;
            case 3:
                freakingJoinTheGameAlready();
                break;
            case 4:
                youWatchMePunk();
                break;
            case 5:
                crashOut();
                break;
            case 6:
                setShouldContinue.set(false);
                break;
            default:
                return;
        }
        KHANSOUL.printf(RESET_TEXT_COLOR);
    }

    private void youNeedHelpMan() {
        KHANSOUL.printf(SET_TEXT_COLOR_GREEN);
        KHANSOUL.printf("\tEnter 0 to see this message\n");
        KHANSOUL.printf("\tEnter 1 to list all available games\n");
        KHANSOUL.printf("\tEnter 2 to create a new game\n");
        KHANSOUL.printf("\tEnter 3 to play a game over the network\n");
        KHANSOUL.printf("\tEnter 4 to observe a game over the network\n");
        KHANSOUL.printf("\tEnter 5 to sign out\n");
        KHANSOUL.printf("\tEnter 6 to close the application and clear credentials\n");
        KHANSOUL.printf(RESET_TEXT_COLOR);
    }

    private void crashOut() {
        try {
            rearend.sayonara();
            setLoggedIn.set(false);
            setShouldContinue.set(true);
            KHANSOUL.printf("Logged out!\n");
        } catch (Exception ex) {
            KHANSOUL.printf(ex.getMessage());
            throw new RuntimeException(ex);

        }
    }

    private void youWatchMePunk() {
        try {
            final var gameId = KHANSOUL.readLine("[Game id]: ");
            int decodedId = GameIdEncoder.decode(gameId);

            var game = rearend.obtenerJuego(decodedId);
            if (game.status() != 200) {
                KHANSOUL.printf("FAILED TO FIND GAME");
                return;
            }

            KHANSOUL.printf(SET_TEXT_COLOR_GREEN + "success" + RESET_TEXT_COLOR);

            var chessGame = game.body().game();
            var asRealObject = new Gson().fromJson(chessGame, ChessGame.class);
            KHANSOUL.printf(asRealObject.prettyPrint(true) + "\n");

        } catch (NumberFormatException ex) {
            KHANSOUL.printf(SET_TEXT_COLOR_RED + "Bro, the ID needs to be an integer dang it."
                    + RESET_TEXT_COLOR);
        } catch (Exception ex) {
            KHANSOUL.printf(ex.getMessage());
            throw new RuntimeException(ex);

        }
    }

    private void freakingJoinTheGameAlready() {
        try {
            final var gameId = KHANSOUL.readLine("[Game id]: ");
            final var color = KHANSOUL.readLine("[" + SET_BG_COLOR_WHITE
                    + SET_TEXT_COLOR_BLACK + "BLACK"
                    + RESET_BG_COLOR + RESET_TEXT_COLOR + "/" + "WHITE" + "]: ");

            if (!"WHITE".equals(color.toUpperCase()) && !"BLACK".equals(color.toUpperCase())) {
                KHANSOUL.printf(SET_TEXT_COLOR_RED + "\tColor must be BLACK or WHITE\n"
                        + RESET_TEXT_COLOR);
                return;
            }

            int decodedId = GameIdEncoder.decode(gameId);

            var game = rearend.obtenerJuego(decodedId);
            if (game.status() != 200) {
                KHANSOUL.printf("FAILED TO FIND GAME");
                return;
            }
            var result = rearend.joinGame(new JoinGamePayload(color.toUpperCase(), decodedId));
            if (result.status() != 200) {
                KHANSOUL.printf("FAILED TO JOIN GAME");
                return;
            }

            KHANSOUL.printf(SET_TEXT_COLOR_GREEN + "success" + RESET_TEXT_COLOR);

            var chessGame = game.body().game();
            var asRealObject = new Gson().fromJson(chessGame, ChessGame.class);
            KHANSOUL.printf(asRealObject.prettyPrint(color.toUpperCase().equals("WHITE")) + "\n");
        } catch (Exception ex) {
            KHANSOUL.printf(ex.getMessage());
            throw new RuntimeException(ex);

        }
    }

    private void makeUpSomeStupidGame() {
        try {
            final var gameName = KHANSOUL.readLine("[Game name]: ");
            final var game = rearend.createGame(new CreateGamePayload(gameName));
            var id = game.body().gameID();
            KHANSOUL.printf(SET_TEXT_COLOR_GREEN + "Game created. (ID: %s)\n" + RESET_TEXT_COLOR,
                    GameIdEncoder.encode(id));

        } catch (Exception ex) {
            KHANSOUL.printf(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    private void gimmeSumGaemz() {
        try {
            var games = rearend.enumerateAllGaemz();

            if (games.status() != 200) {
                KHANSOUL.printf(SET_TEXT_COLOR_RED + "Error. Status %d\n"
                        + RESET_TEXT_COLOR,
                        games.status());
            } else {
                final var allGames = games.body().games();
                KHANSOUL.printf(SET_TEXT_COLOR_GREEN);
                if (allGames.size() == 0) {
                    KHANSOUL.printf("No games! Create one to play.\n");
                    return;
                }
                int gameNum = 1;
                KHANSOUL.printf("\n[GAMES]\n");
                for (final var game : allGames) {
                    KHANSOUL.printf(gameNum + ". Game: %s [%s vs %s]\n", game.gameName(),
                            game.whiteUsername(),
                            game.blackUsername());
                }
                KHANSOUL.printf(RESET_TEXT_COLOR);
                gameNum++;
            }

        } catch (Exception yourEx) {
            KHANSOUL.printf(yourEx.getMessage());
            throw new RuntimeException(yourEx);

        }
    }
}
