package pl.edu.devtools.core.urls;

/**
 * Tydzień 2, Wzorzec 5: Memento, zastosowanie 1.
 * Migawka ukrywa głęboką kopię podglądu i odtwarza z niej niezależne drzewo.
 */
final class UrlMemento {
    private final UrlGroup batch;

    UrlMemento(UrlGroup batch) {
        this.batch = batch.copy();
    }

    UrlGroup restore() {
        return batch.copy();
    }
}
/** Koniec, Tydzień 2, Wzorzec 5: Memento, zastosowanie 1. */
