import java.io.Console;
import java.util.Random;

import client.ServerFacade;
import dto.LoginPayload;
import dto.RegisterPayload;
import ui.Prompt;

final class LoggedOutCommands {
    private String username;
    private String password;
    private String email;

    private static final Console CONSOLE = System.console();

    public interface BooleanSetter {
        void set(boolean val);
    }

    public interface BooleanGetter {
        boolean get();
    }

    private final ServerFacade backend;
    private final BooleanSetter setLoggedIn;
    private final BooleanGetter getLoggedIn;
    private final BooleanSetter setShouldContinue;

    public LoggedOutCommands(ServerFacade serverFacade, BooleanSetter setLoggedIn, BooleanGetter getLoggedIn,
            BooleanSetter setShouldContinue) {
        backend = serverFacade;
        this.setLoggedIn = setLoggedIn;
        this.getLoggedIn = getLoggedIn;
        this.setShouldContinue = setShouldContinue;
    }

    public void getUsernamePassword() {
        new Prompt("[Enter your username]").run(u -> {
            username = u;
        });
        new Prompt("[Enter your password]").run(p -> {
            password = p;
        });
    }

    private void login() {
        try {
            getUsernamePassword();
            var response = backend.login(new LoginPayload(username, password));

            if (response.status() == 200) {
                setLoggedIn.set(true);
                password = null;
            } else if (response.status() == 401){
                CONSOLE.printf("\n[ERROR]: Username or password is incorrect.\n");
            }
            else {
                CONSOLE.printf("Sorry. Something went terribly wrong. Go play chess on chess.com");
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void register() {
        try {
            getUsernamePassword();
            new Prompt("[Enter your email]").run(e -> {
                email = e;
            });
            var response = backend.register(new RegisterPayload(username, password, email));

            if (response.status() == 200) {
                setLoggedIn.set(true);
                password = null;
            } else if (response.status() == 403){
                CONSOLE.printf("\n[ERROR]: Username already taken.\n");
            } 
            else if (response.status() == 401){
                CONSOLE.printf("\n[ERROR]: Username or password is incorrect.\n");
            }
            else {
                CONSOLE.printf("Sorry. Something went terribly wrong. Go play chess somewhere else or touch some grass or something.");
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void quit() {
        try {
            if (getLoggedIn.get()) {
                backend.logout();
            }
            setShouldContinue.set(false);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void handleLoggedOut(int input) {
        switch (input) {
            case 0:
                CONSOLE.printf("\tEnter a number to select one of the options.");
                CONSOLE.printf("\tIf you lack an account, register. Otherwise, log in.");
                CONSOLE.printf("\tDo not be stupid and delete the database.");
                break;
            case 1:
                login();
                break;
            case 2:
                register();
                break;
            case 3:
                quit();
                break;
            case 4:
                beStupid();
                break;
            default:
                break;
        }

    }

    private void beStupid() {
        try {
            var sure = CONSOLE.readLine("Are you sure? [y/N]").trim();
            if (!sure.toLowerCase().equals("y")) {
                if (sure.toLowerCase().equals("sudo delete everything")){
                    backend.clearDb();
                    CONSOLE.printf("DATABASE DELETED");
                    return;
                }
                return;
            }
            sure = CONSOLE.readLine("Are you still sure? [y/N]").trim();
            if (!sure.toLowerCase().equals("y")) {
                return;
            }
            CONSOLE.printf("This is a terrible idea.\n");
            sure = CONSOLE.readLine("Are you sure you want to clear the whole db? [y/N]").trim();
            if (!sure.toLowerCase().equals("y")) {
                return;
            }
            sure = CONSOLE.readLine("You are a terrible person. Do you still want to continue? [y/N]").trim();
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
            CONSOLE.printf("Saving your name to a backup location so we can blame you later...\n");
            Thread.sleep(new Random().nextInt(1000, 10000));
            doesContinue = new Random().nextInt(0, 10);
            if (doesContinue < 7) {
                CONSOLE.printf("Hmm... Network issue. Sorry kid. Not really...\n");
                setShouldContinue.set(false);
                return;
            }
            backend.clearDb();
            CONSOLE.printf("The database has been cleared. You will live forever in infamy.\n");
            setShouldContinue.set(false);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
