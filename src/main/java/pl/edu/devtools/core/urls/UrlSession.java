package pl.edu.devtools.core.urls;

import pl.edu.devtools.core.UrlNode;
import pl.edu.devtools.core.urls.observers.UrlSessionObservableSupport;
import pl.edu.devtools.core.urls.states.EmptyState;
import pl.edu.devtools.core.urls.states.ReadyState;
import pl.edu.devtools.core.urls.states.UrlState;

import java.util.Objects;

/**
 * Lab 2, zadanie 1: Command, zastosowanie 2.
 * Odbiorca poleceń przechowuje podgląd i wykonuje operacje na jego danych.
 */
public final class UrlSession extends UrlSessionObservableSupport {
    private UrlGroup batch;
    private UrlGroup prototype;
    private final UrlHistory history = new UrlHistory();
    private UrlState state = new EmptyState();

    @Override
    public UrlGroup batch() {
        return batch;
    }

    public void load(UrlGroup original) {
        state.requireEditable();
        Objects.requireNonNull(original);
        /**
         * Lab 1, zadanie 3: Prototype, zastosowanie 3.
         * Zapamiętuje głęboką kopię wygenerowanego podglądu. Usuwanie adresów
         * z batch nie zmieni tej kopii używanej przez Przywróć podgląd.
         * Kopia robocza batch powstaje z zapisanego prototypu.
         */
        prototype = original.copy();
        batch = prototype.copy();
        /** Koniec, Lab 1, zadanie 3: Prototype, zastosowanie 3. */
        history.clear();
        state = new ReadyState();
        notifyObservers();
    }

    public void clear() {
        state.requireEditable();
        batch = null;
        prototype = null;
        history.clear();
        state = new EmptyState();
        notifyObservers();
    }

    public void remove(UrlGroup parent, UrlNode node) {
        state.requireEditable();
        if (batch != null && containsGroup(batch, parent) && parent.children().contains(node)) {
            remember();
            parent.remove(node);
            notifyObservers();
        }
    }

    private boolean containsGroup(UrlGroup root, UrlGroup target) {
        return root == target || root.children().stream()
                .anyMatch(child -> child instanceof UrlGroup group && containsGroup(group, target));
    }

    public void restore() {
        state.requireEditable();
        if (prototype != null) {
            /**
             * Lab 1, zadanie 3: Prototype, zastosowanie 2.
             * Odtworzenie niezależnego drzewa z prototypu zapisanego po generowaniu.
             */
            UrlGroup restored = prototype.copy();
            remember();
            batch = restored;
            /** Koniec, Lab 1, zadanie 3: Prototype, zastosowanie 2. */
            notifyObservers();
        }
    }

    /**
     * Lab 2, zadanie 5: Memento, zastosowanie 2.
     * Originator zapisuje stan kopii roboczej przed jej zmianą.
     */
    private void remember() {
        history.save(new UrlMemento(batch));
    }
    /** Koniec, Lab 2, zadanie 5: Memento, zastosowanie 2. */

    @Override
    public boolean canUndo() {
        return canEdit() && history.previous() != null;
    }

    /**
     * Lab 2, zadanie 5: Memento, zastosowanie 3.
     * Każde wywołanie odtwarza ostatnią migawkę i usuwa tylko ją z historii.
     */
    public void undo() {
        state.requireEditable();
        UrlMemento previous = history.previous();
        if (previous != null) {
            batch = previous.restore();
            history.discardPrevious();
            notifyObservers();
        }
    }
    /** Koniec, Lab 2, zadanie 5: Memento, zastosowanie 3. */

    /**
     * Lab 2, zadanie 7: State, zastosowanie 2.
     * Kontekst deleguje wywołanie przejść i dostępność edycji do bieżącego stanu.
     */
    @Override
    public boolean canEdit() {
        return state.canEdit();
    }

    public UrlGroup beginOpening() {
        UrlState next = state.beginOpening();
        if (!batch.iterator().hasNext()) {
            throw new IllegalStateException("Podgląd jest pusty. Przywróć adresy lub zmień parametry.");
        }
        UrlGroup snapshot = batch.copy();
        state = next;
        notifyObservers();
        return snapshot;
    }

    public void finishOpening() {
        UrlState next = state.finishOpening();
        if (next != state) {
            state = next;
            notifyObservers();
        }
    }
    /** Koniec, Lab 2, zadanie 7: State, zastosowanie 2. */
}
/** Koniec, Lab 2, zadanie 1: Command, zastosowanie 2. */
