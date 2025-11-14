package ui;

import java.io.Console;

public final class Prompt {
    private static Console console = System.console();

    private String promptText;
    private String promptSuffix = ">>>";

    public interface RighteousHandlerOfPrompts {
        void handleInput(String input);
    };

    public interface IntPromptHandler {
        void handleInput(int input);
    };

    public Prompt(String promptText) {
        this.promptText = promptText;
    }

    public Prompt(String promptText, String promptSuffix) {
        this.promptText = promptText;
        this.promptSuffix = promptSuffix;
    }

    public void setPromptText(String text) {
        promptText = text;
    }

    public void setPromptSuffix(String text) {
        promptSuffix = text;
    }

    public void runButGetAnIntegerInsteadOfAString(IntPromptHandler handler) {
        boolean valid = false;
        do {
            System.out.printf("%s %s ", promptText, promptSuffix);
            final var input = console.readLine();
            try {
                var integer = Integer.parseInt(input);
                valid = true;
                handler.handleInput(integer);
            } catch (NumberFormatException nfe) {
                if (input.trim().startsWith("q")) {
                    break;
                }
                continue;
            }
        } while (!valid);
    }

    public void executeThePromptOrderOfTheKing(RighteousHandlerOfPrompts handler) {
        System.out.printf("%s %s ", promptText, promptSuffix);
        final var value = console.readLine();
        handler.handleInput(value);
    }
}
