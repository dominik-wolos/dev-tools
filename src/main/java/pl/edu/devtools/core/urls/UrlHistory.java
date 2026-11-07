package pl.edu.devtools.core.urls;

/**
 * Lab 2, zadanie 5: Memento, zastosowanie 4.
 * Opiekun przechowuje jedną migawkę bez dostępu do jej zawartości.
 */
final class UrlHistory {
    private UrlMemento previous;

    void save(UrlMemento memento) {
        previous = memento;
    }

    UrlMemento previous() {
        return previous;
    }

    void clear() {
        previous = null;
    }
}
/** Koniec, Lab 2, zadanie 5: Memento, zastosowanie 4. */
