package pl.edu.devtools.core.urls;

import pl.edu.devtools.core.UrlNode;

/**
 * Tydzień 2, Wzorzec 5: Memento, zastosowanie 1.
 * Migawka ukrywa głęboką kopię podglądu i odtwarza z niej niezależne drzewo.
 */
final class UrlMemento {
    private final UrlNode.Group batch;

    UrlMemento(UrlNode.Group batch) {
        this.batch = batch.copy();
    }

    UrlNode.Group restore() {
        return batch.copy();
    }
}
/** Koniec, Tydzień 2, Wzorzec 5: Memento, zastosowanie 1. */
