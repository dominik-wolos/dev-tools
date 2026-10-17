package pl.edu.devtools.core.urls.observers;

import pl.edu.devtools.core.UrlNode;

/** Tydzień 2, Wzorzec 6: Observer — kontrakt obserwowanej sesji i odczytu jej stanu. */
public interface UrlSessionObservable {
    void addObserver(UrlSessionObserver observer);

    void removeObserver(UrlSessionObserver observer);

    UrlNode.Group batch();

    boolean canEdit();

    boolean canUndo();
}
