package pl.edu.devtools.core.urls.commands;

import pl.edu.devtools.core.urls.UrlSession;

/**
 * Tydzień 2, Wzorzec 1: Command, zastosowanie 4.
 * Konkretne polecenie zleca odbiorcy odtworzenie podglądu z prototypu.
 */
public record RestorePreviewCommand(UrlSession session) implements Command {
    @Override
    public void execute() {
        session.restore();
    }
}
/** Koniec, Tydzień 2, Wzorzec 1: Command, zastosowanie 4. */
