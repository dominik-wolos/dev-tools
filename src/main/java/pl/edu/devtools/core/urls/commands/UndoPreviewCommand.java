package pl.edu.devtools.core.urls.commands;

import pl.edu.devtools.core.urls.UrlSession;

/**
 * Lab 2, zadanie 1: Command, zastosowanie 6.
 * Polecenie zleca odbiorcy cofnięcie ostatniej zmiany podglądu.
 */
public record UndoPreviewCommand(UrlSession session) implements Command {
    @Override
    public void execute() {
        session.undo();
    }
}
/** Koniec, Lab 2, zadanie 1: Command, zastosowanie 6. */
