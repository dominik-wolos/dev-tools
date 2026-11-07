package pl.edu.devtools.core.urls.commands;

import pl.edu.devtools.core.UrlNode;
import pl.edu.devtools.core.urls.UrlGroup;
import pl.edu.devtools.core.urls.UrlSession;

/**
 * Lab 2, zadanie 1: Command, zastosowanie 3.
 * Konkretne polecenie przechowuje odbiorcę i węzeł do usunięcia z podglądu.
 */
public record RemoveUrlCommand(UrlSession session, UrlGroup parent, UrlNode node) implements Command {
    @Override
    public void execute() {
        session.remove(parent, node);
    }
}
/** Koniec, Lab 2, zadanie 1: Command, zastosowanie 3. */
