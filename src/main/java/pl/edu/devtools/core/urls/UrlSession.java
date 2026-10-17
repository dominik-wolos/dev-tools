package pl.edu.devtools.core.urls;

import pl.edu.devtools.core.UrlNode;

import java.util.Objects;

/**
 * Tydzień 2, Wzorzec 1: Command, zastosowanie 2.
 * Odbiorca poleceń przechowuje podgląd i wykonuje operacje na jego danych.
 */
public final class UrlSession {
    private UrlNode.Group batch;
    private UrlNode.Group prototype;
    private final UrlHistory history = new UrlHistory();

    public UrlNode.Group batch() {
        return batch;
    }

    public void load(UrlNode.Group original) {
        Objects.requireNonNull(original);
        /**
         * Tydzień 1, Wzorzec 3/9: Prototype, zastosowanie 3.
         * Zapamiętuje głęboką kopię wygenerowanego podglądu. Usuwanie adresów
         * z batch nie zmieni tej kopii używanej przez Przywróć podgląd.
         * Kopia robocza batch powstaje z zapisanego prototypu.
         */
        prototype = original.copy();
        batch = prototype.copy();
        /** Koniec, Tydzień 1, Wzorzec 3/9: Prototype, zastosowanie 3. */
        history.clear();
    }

    public void clear() {
        batch = null;
        prototype = null;
        history.clear();
    }

    public void remove(UrlNode.Group parent, UrlNode node) {
        if (batch != null && containsGroup(batch, parent) && parent.children().contains(node)) {
            remember();
            parent.remove(node);
        }
    }

    private boolean containsGroup(UrlNode.Group root, UrlNode.Group target) {
        return root == target || root.children().stream()
                .anyMatch(child -> child instanceof UrlNode.Group group && containsGroup(group, target));
    }

    public void restore() {
        if (prototype != null) {
            /**
             * Tydzień 1, Wzorzec 3/9: Prototype, zastosowanie 2.
             * Odtworzenie niezależnego drzewa z prototypu zapisanego po generowaniu.
             */
            UrlNode.Group restored = prototype.copy();
            remember();
            batch = restored;
            /** Koniec, Tydzień 1, Wzorzec 3/9: Prototype, zastosowanie 2. */
        }
    }

    /**
     * Tydzień 2, Wzorzec 5: Memento, zastosowanie 2.
     * Originator zapisuje stan kopii roboczej przed jej zmianą.
     */
    private void remember() {
        history.save(new UrlMemento(batch));
    }
    /** Koniec, Tydzień 2, Wzorzec 5: Memento, zastosowanie 2. */

    public boolean canUndo() {
        return history.previous() != null;
    }

    /**
     * Tydzień 2, Wzorzec 5: Memento, zastosowanie 3.
     * Jednokrotne wywołanie odtworzenia migawki przywraca ostatni stan batch.
     */
    public void undo() {
        UrlMemento previous = history.previous();
        if (previous != null) {
            batch = previous.restore();
            history.clear();
        }
    }
    /** Koniec, Tydzień 2, Wzorzec 5: Memento, zastosowanie 3. */
}
/** Koniec, Tydzień 2, Wzorzec 1: Command, zastosowanie 2. */
