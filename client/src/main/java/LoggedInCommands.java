import java.io.Console;
import java.util.Random;

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
                logout();
                break;
            case 5:
                beStupid();
                break;
            case 6:
                setShouldContinue.set(false);
                break;
            default:
                return;
        }
    }

    private void beStupid() {
        try {
            var sure = CONSOLE.readLine("Are you sure? [y/N]");
            if (!sure.toLowerCase().equals("y")) {
                return;
            }
            sure = CONSOLE.readLine("Are you still sure? [y/N]");
            if (!sure.toLowerCase().equals("y")) {
                return;
            }
            CONSOLE.printf("This is a terrible idea.\n");
            sure = CONSOLE.readLine("Are you sure you want to clear the whole db? [y/N]");
            if (!sure.toLowerCase().equals("y")) {
                return;
            }
            sure = CONSOLE.readLine("You are a terrible person. Do you still want to continue? [y/N]");
            if (!sure.toLowerCase().equals("y")) {
                return;
            }
            CONSOLE.printf("Clearing the database...\n");
            Thread.sleep(new Random().nextInt(1000, 10000));
            CONSOLE.printf("Considering life decisions...\n");
            Thread.sleep(new Random().nextInt(1000, 10000));
            CONSOLE.printf("Writing goodbyes to family members...\n");
            Thread.sleep(new Random().nextInt(1000, 10000));
            CONSOLE.printf("Telling my wife I loved her and will love her forever...\n");
            Thread.sleep(new Random().nextInt(1000, 10000));
            CONSOLE.printf("This is taking longer than expected...\n");
            Thread.sleep(new Random().nextInt(1000, 10000));
            CONSOLE.printf("Oh my gosh you should press CTRL+C...\n");
            Thread.sleep(new Random().nextInt(1000, 10000));
            CONSOLE.printf("Thinking maybe I have a lot to live for and considering backing out...\n");
            var doesContinue = new Random().nextInt(0, 10);
            if (doesContinue < 7) {
                CONSOLE.printf("I decided not to clear the database. Sorry for the inconvenience.\n");
                return;
            }
            Thread.sleep(new Random().nextInt(1000, 10000));
            CONSOLE.printf("Oh wait, no! The button worked?...\n");
            Thread.sleep(new Random().nextInt(1000, 10000));
            backend.clearDb();
            CONSOLE.printf("The database has been cleared. You will live forever in infamy.\n");
        }

        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void logout() {
        try {
            backend.logout();
            setLoggedIn.set(false);
            CONSOLE.printf("Logged out!\n");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void joinGame() {
        try {
            final var gameId = CONSOLE.readLine("[Game id]: ");
            final var color = CONSOLE.readLine("[BLACK/WHITE]: ");

            if (!"WHITE".equals(color.toUpperCase()) && !"BLACK".equals(color.toUpperCase())){
                CONSOLE.printf("Color must be BLACK or WHITE");
                return;
            }

            var gameIdInteger = Integer.parseInt(gameId);

            backend.joinGame(new JoinGamePayload(color.toUpperCase(), gameIdInteger));
            CONSOLE.printf("success");

        } catch (NumberFormatException ex) {
            CONSOLE.printf("Bro, the ID needs to be an integer dang it.");
        } 
        catch (Exception ex) {
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
                CONSOLE.printf("Error. Status %d\n", games.status());
            } else {
                final var allGames = games.body().games();
                if (allGames.size() == 0) {
                    CONSOLE.printf("No games! Create one to play.\n");
                    return;
                }
                CONSOLE.printf("\n[GAMES]\n");
                for (final var game : allGames) {
                    CONSOLE.printf("Game: %s (id %d) [%s vs %s]\n", game.gameName(), game.gameID(),
                            game.whiteUsername(),
                            game.blackUsername());
                }
            }

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
