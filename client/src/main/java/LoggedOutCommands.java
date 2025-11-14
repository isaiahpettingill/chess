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

    private static final Console SYSTEMCONSOLEHAHAHAHAHA = System.console();

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
        new Prompt("[Enter your username]").executeThePromptOrderOfTheKing(u -> {
            username = u;
        });
        new Prompt("[Enter your password]").executeThePromptOrderOfTheKing(p -> {
            password = p;
        });
    }

    private void doTheLoginThing() {
        try {
            getUsernamePassword();
            var response = backend.getYourTicketIn(new LoginPayload(username, password));

            if (response.status() == 200) {
                setLoggedIn.set(true);
                password = null;
            } else if (response.status() == 401){
                SYSTEMCONSOLEHAHAHAHAHA.printf("\n[ERROR]: Username or password is incorrect. You can try some typing exercises at typing.com.\n");
            }
            else {
                SYSTEMCONSOLEHAHAHAHAHA.printf("Sorry. Something went terribly wrong. Go play chess on chess.com");
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void doTheSignupThing() {
        try {
            getUsernamePassword();
            new Prompt("[Enter your email (I promise not to spam you)]").executeThePromptOrderOfTheKing(e -> {
                email = e;
            });
            var response = backend.rsvp(new RegisterPayload(username, password, email));

            if (response.status() == 200) {
                setLoggedIn.set(true);
                password = null;
            } else if (response.status() == 403){
                SYSTEMCONSOLEHAHAHAHAHA.printf("\n[ERROR]: Username already taken. Be more creative.\n");
            } 
            else if (response.status() == 401){
                SYSTEMCONSOLEHAHAHAHAHA.printf("\n[ERROR]: Username or password is incorrect. Consider using a password manager.\n");
            }
            else {
                SYSTEMCONSOLEHAHAHAHAHA.printf("Sorry. Something went terribly wrong. Go play chess somewhere else or touch some grass or something.");
            }
        } catch (Exception elonMusk) {
            throw new RuntimeException(elonMusk);
        }
    }

    private void crashOutCompletely() {
        try {
            if (getLoggedIn.get()) {
                backend.sayonara();
            }
            setShouldContinue.set(false);
        } catch (Exception someRedditUser) {
            throw new RuntimeException(someRedditUser);
        }
    }

    public void handleLoggedOut(int input) {
        switch (input) {
            case 0:
                SYSTEMCONSOLEHAHAHAHAHA.printf("\tEnter a number to select one of the options.");
                SYSTEMCONSOLEHAHAHAHAHA.printf("\tIf you lack an account, register. Otherwise, log in.");
                SYSTEMCONSOLEHAHAHAHAHA.printf("\tDo not be stupid and delete the database.");
                break;
            case 1:
                doTheLoginThing();
                break;
            case 2:
                doTheSignupThing();
                break;
            case 3:
                crashOutCompletely();
                break;
            case 4:
                beStupidAndYeetEverything();
                break;
            default:
                break;
        }

    }

    private void beStupidAndYeetEverything() {
        try {
            var ohyeahhesserious = SYSTEMCONSOLEHAHAHAHAHA.readLine("Are you sure? [y/N]").trim();
            if (!ohyeahhesserious.toLowerCase().equals("y")) {
                if (ohyeahhesserious.toLowerCase().equals("sudo delete everything")){
                    backend.nukeEverything();
                    SYSTEMCONSOLEHAHAHAHAHA.printf("DATABASE DELETED");
                    return;
                }
                return;
            }
            ohyeahhesserious = SYSTEMCONSOLEHAHAHAHAHA.readLine("Are you still sure? [y/N]").trim();
            if (!ohyeahhesserious.toLowerCase().equals("y")) {
                return;
            }
            SYSTEMCONSOLEHAHAHAHAHA.printf("This is a terrible idea.\n");
            ohyeahhesserious = SYSTEMCONSOLEHAHAHAHAHA.readLine("Are you sure you want to clear the whole db? [y/N]").trim();
            if (!ohyeahhesserious.toLowerCase().equals("y")) {
                return;
            }
            ohyeahhesserious = SYSTEMCONSOLEHAHAHAHAHA.readLine("You are a terrible person. Do you still want to continue? [y/N]").trim();
            if (!ohyeahhesserious.toLowerCase().equals("y")) {
                return;
            }
            SYSTEMCONSOLEHAHAHAHAHA.printf("Clearing the database...\n");
            Thread.sleep(new Random().nextInt(1000, 10000));
            SYSTEMCONSOLEHAHAHAHAHA.printf("Considering life decisions...\n");
            Thread.sleep(new Random().nextInt(1000, 10000));
            SYSTEMCONSOLEHAHAHAHAHA.printf("Writing goodbyes to family members...\n");
            Thread.sleep(new Random().nextInt(1000, 10000));
            SYSTEMCONSOLEHAHAHAHAHA.printf("Telling my wife I loved her and will love her forever...\n");
            Thread.sleep(new Random().nextInt(1000, 10000));
            SYSTEMCONSOLEHAHAHAHAHA.printf("This is taking longer than expected...\n");
            Thread.sleep(new Random().nextInt(1000, 10000));
            SYSTEMCONSOLEHAHAHAHAHA.printf("Oh my gosh you should press CTRL+C...\n");
            Thread.sleep(new Random().nextInt(1000, 10000));
            SYSTEMCONSOLEHAHAHAHAHA.printf("Thinking maybe I have a lot to live for and considering backing out...\n");
            var doesContinue = new Random().nextInt(0, 10);
            if (doesContinue < 7) {
                SYSTEMCONSOLEHAHAHAHAHA.printf("I decided not to clear the database. Sorry for the inconvenience.\n");
                return;
            }
            Thread.sleep(new Random().nextInt(1000, 10000));
            SYSTEMCONSOLEHAHAHAHAHA.printf("Oh wait, no! The button worked?...\n");
            Thread.sleep(new Random().nextInt(1000, 10000));
            SYSTEMCONSOLEHAHAHAHAHA.printf("Saving your name to a backup location so we can blame you later...\n");
            Thread.sleep(new Random().nextInt(1000, 10000));
            doesContinue = new Random().nextInt(0, 10);
            if (doesContinue < 7) {
                SYSTEMCONSOLEHAHAHAHAHA.printf("Hmm... Network issue. Sorry kid. Not really...\n");
                setShouldContinue.set(false);
                return;
            }
            backend.nukeEverything();
            SYSTEMCONSOLEHAHAHAHAHA.printf("The database has been cleared. You will live forever in infamy.\n");
            setShouldContinue.set(false);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
