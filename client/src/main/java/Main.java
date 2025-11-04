import chess.*;
import client.ServerFacade;
import ui.Prompt;

import static ui.Prompts.*;

public class Main {
    private static ServerFacade backend = new ServerFacade("localhost:7070");
    public static void main(String[] args) {
        final var prompt = new Prompt(LOGGED_OUT);
        prompt.run(r -> {});
    }
}
