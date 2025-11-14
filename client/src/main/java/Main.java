import client.ServerFacade;
import ui.Prompt;

import static ui.Prompts.*;

public final class Main {
    private static ServerFacade backend = new ServerFacade("http://localhost:7070");

    private static boolean loggedIn = false;
    private static boolean shouldContinue = true;

    private static LoggedOutCommands loggedOutCommands = new LoggedOutCommands(backend, li -> loggedIn = li,
            () -> loggedIn, x -> shouldContinue = x);
    private static LoggedInCommands loggedInCommands = new LoggedInCommands(backend, x -> shouldContinue = x,
            vx -> loggedIn = vx);

    public static void main(String[] args) {

        final var prompt = new Prompt(LOGGED_OUT);

        while (shouldContinue) {
            if (loggedIn) {
                prompt.setPromptText(LOGGED_IN);
                prompt.runInt(loggedInCommands::handleLoggedIn);
            } else {
                prompt.setPromptText(LOGGED_OUT);
                prompt.runInt(loggedOutCommands::handleLoggedOut);
            }
        }
    }
}
