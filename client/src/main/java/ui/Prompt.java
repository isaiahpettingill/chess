package ui;

import java.util.Scanner;

public final class Prompt {
    private String promptText;
    private String promptSuffix = ">>>";
    
    public interface PromptHandler {
        void handleInput(String input);
    };

    public Prompt(String promptText) {
        this.promptText = promptText;
    }

    public Prompt(String promptText, String promptSuffix) {
        this.promptText = promptText;
        this.promptSuffix = promptSuffix;
    }

    public void setPromptText(String text){
        promptText = text;
    }

    public void setPromptSuffix(String text){
        promptSuffix = text;
    }

    public void run(PromptHandler handler) {
        System.out.printf("%s %s ", promptText, promptSuffix);
        try (final var scanner = new Scanner(System.in)) {
            final var result = scanner.nextLine();
            handler.handleInput(result);
        }
    }
}
