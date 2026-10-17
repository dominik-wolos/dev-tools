package pl.edu.devtools.core.urls;

import pl.edu.devtools.core.UrlNode;
import pl.edu.devtools.core.urls.states.EmptyState;
import pl.edu.devtools.core.urls.states.ReadyState;
import pl.edu.devtools.core.urls.states.UrlState;

import java.util.Objects;

/**
 * Tydzień 2, Wzorzec 1: Command, zastosowanie 2.
 * Odbiorca poleceń przechowuje podgląd i wykonuje operacje na jego danych.
 */
public final class UrlSession {
    private UrlNode.Group batch;
    private UrlNode.Group prototype;
    private final UrlHistory history = new UrlHistory();
    private UrlState state = new EmptyState();

    public UrlNode.Group batch() {
        return batch;
    }

    public void load(UrlNode.Group original) {
        state.requireEditable();
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
        state = new ReadyState();
    }

    public void clear() {
        state.requireEditable();
        batch = null;
        prototype = null;
        history.clear();
        state = new EmptyState();
    }

    public void remove(UrlNode.Group parent, UrlNode node) {
        state.requireEditable();
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
        state.requireEditable();
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
        return canEdit() && history.previous() != null;
    }

    /**
     * Tydzień 2, Wzorzec 5: Memento, zastosowanie 3.
     * Jednokrotne wywołanie odtworzenia migawki przywraca ostatni stan batch.
     */
    public void undo() {
        state.requireEditable();
        UrlMemento previous = history.previous();
        if (previous != null) {
            batch = previous.restore();
            history.clear();
        }
    }
    /** Koniec, Tydzień 2, Wzorzec 5: Memento, zastosowanie 3. */

    /**
     * Tydzień 2, Wzorzec 7: State, zastosowanie 2.
     * Kontekst deleguje wywołanie przejść i dostępność edycji do bieżącego stanu.
     */
    public boolean canEdit() {
        return state.canEdit();
    }

    public UrlNode.Group beginOpening() {
        UrlState next = state.beginOpening();
        if (!batch.iterator().hasNext()) {
            throw new IllegalStateException("Podgląd jest pusty. Przywróć adresy lub zmień parametry.");
        }
        UrlNode.Group snapshot = batch.copy();
        state = next;
        return snapshot;
    }

    public void finishOpening() {
        state = state.finishOpening();
    }
    /** Koniec, Tydzień 2, Wzorzec 7: State, zastosowanie 2. */
}
/** Koniec, Tydzień 2, Wzorzec 1: Command, zastosowanie 2. */
