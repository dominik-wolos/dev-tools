package pl.edu.devtools.core.urls.commands;

import pl.edu.devtools.core.urls.UrlSession;

/**
 * Lab 2, zadanie 1: Command, zastosowanie 4.
 * Konkretne polecenie zleca odbiorcy odtworzenie podglądu z prototypu.
 */
public record RestorePreviewCommand(UrlSession session) implements Command {
    @Override
    public void execute() {
        session.restore();
    }
}
/** Koniec, Lab 2, zadanie 1: Command, zastosowanie 4. */
