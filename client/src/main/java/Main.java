import client.ServerFacade;
import dto.*;
import ui.Prompt;

import static ui.Prompts.*;

public final class Main {
    private static ServerFacade backend = new ServerFacade("localhost:7070");

    private static String username;
    private static String password;
    private static String email;
    private static boolean loggedIn = false;
    private static boolean shouldContinue = true;

    private static void handleLoggedIn(String input) {

    }

    private static void getUsernamePassword() {
        new Prompt("[Enter your username]").run(u -> {
            username = u;
        });
        new Prompt("[Enter your password]").run(p -> {
            password = p;
        });
    }

    private static void handleLoggedOut(String input) {
        try {
            switch (input.trim()) {
                case "1":
                    getUsernamePassword();
                    backend.login(new LoginPayload(username, password));
                    loggedIn = true;
                    password = null;
                    break;
                case "2":
                    getUsernamePassword();
                    new Prompt("[Enter your email]").run(e -> {
                        email = e;
                    });
                    backend.register(new RegisterPayload(username, password, email));
                    loggedIn = true;
                    password = null;
                    break;
                case "3":
                    if (loggedIn) {
                        backend.logout();
                    }
                    shouldContinue = false;
                    break;
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void main(String[] args) {

        final var prompt = new Prompt(LOGGED_OUT);

        while (shouldContinue) {
            if (loggedIn) {
                prompt.setPromptText(LOGGED_IN);
                prompt.run(Main::handleLoggedIn);
            } else {
                prompt.setPromptText(LOGGED_OUT);
                prompt.run(Main::handleLoggedOut);
            }
        }
    }
}
