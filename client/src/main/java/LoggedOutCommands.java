import client.ServerFacade;
import dto.LoginPayload;
import dto.RegisterPayload;
import ui.Prompt;

final class LoggedOutCommands {
    private String username;
    private String password;
    private String email;

    public interface BooleanSetter{
        void set(boolean val);
    }

    public interface BooleanGetter {
        boolean get();
    }


    private final ServerFacade backend;
    private final BooleanSetter setLoggedIn;
    private final BooleanGetter getLoggedIn;
    private final BooleanSetter setShouldContinue;

    public LoggedOutCommands(ServerFacade serverFacade, BooleanSetter setLoggedIn, BooleanGetter getLoggedIn, BooleanSetter setShouldContinue){
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
                case 1:
                    getUsernamePassword();
                    backend.login(new LoginPayload(username, password));
                    setLoggedIn.set(true);
                    password = null;
                    break;
                case 2:
                    getUsernamePassword();
                    new Prompt("[Enter your email]").run(e -> {
                        email = e;
                    });
                    backend.register(new RegisterPayload(username, password, email));
                    setLoggedIn.set(true);
                    password = null;
                    break;
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
