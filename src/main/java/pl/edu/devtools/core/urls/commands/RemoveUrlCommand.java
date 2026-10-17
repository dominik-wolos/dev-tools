package pl.edu.devtools.core.urls.commands;

import pl.edu.devtools.core.UrlNode;
import pl.edu.devtools.core.urls.UrlSession;

/**
 * Tydzień 2, Wzorzec 1: Command, zastosowanie 3.
 * Konkretne polecenie przechowuje odbiorcę i węzeł do usunięcia z podglądu.
 */
public record RemoveUrlCommand(UrlSession session, UrlNode.Group parent, UrlNode node) implements Command {
    @Override
    public void execute() {
        session.remove(parent, node);
    }
}
/** Koniec, Tydzień 2, Wzorzec 1: Command, zastosowanie 3. */
