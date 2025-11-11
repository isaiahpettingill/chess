import java.io.Console;

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

    public void handleLoggedOut(int input) {
        try {
            switch (input) {
                case 1: {
                    getUsernamePassword();
                    var response = backend.login(new LoginPayload(username, password));

                    if (response.status() == 200) {
                        setLoggedIn.set(true);
                        password = null;
                    } else {
                        CONSOLE.printf("\n[ERROR]: Dude, you did something wrong.\n");
                    }
                    break;
                }
                case 2: {
                    getUsernamePassword();
                    new Prompt("[Enter your email]").run(e -> {
                        email = e;
                    });
                    var response = backend.register(new RegisterPayload(username, password, email));

                    if (response.status() == 200) {
                        setLoggedIn.set(true);
                        password = null;
                    } else {
                        CONSOLE.printf("\n[ERROR]: Dude, you did something wrong.\n");
                    }
                    break;
                }
                case 3:
                    if (getLoggedIn.get()) {
                        backend.logout();
                    }
                    setShouldContinue.set(false);
                    break;
                default:
                    break;
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
