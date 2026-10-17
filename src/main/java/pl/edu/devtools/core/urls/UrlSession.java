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

    public UrlNode.Group batch() {
        return batch;
    }

    public void load(UrlNode.Group batch) {
        Objects.requireNonNull(batch);
        /**
         * Tydzień 1, Wzorzec 3/9: Prototype, zastosowanie 3.
         * Zapamiętuje głęboką kopię wygenerowanego podglądu. Usuwanie adresów
         * z batch nie zmieni tej kopii używanej przez Przywróć podgląd.
         */
        prototype = batch.copy();
        /** Koniec, Tydzień 1, Wzorzec 3/9: Prototype, zastosowanie 3. */
        this.batch = batch;
    }

    public void clear() {
        batch = null;
        prototype = null;
    }

    public void remove(UrlNode.Group parent, UrlNode node) {
        parent.remove(node);
    }

    public void restore() {
        if (prototype != null) {
            /**
             * Tydzień 1, Wzorzec 3/9: Prototype, zastosowanie 2.
             * Odtworzenie niezależnego drzewa z prototypu zapisanego po generowaniu.
             */
            batch = prototype.copy();
            /** Koniec, Tydzień 1, Wzorzec 3/9: Prototype, zastosowanie 2. */
        }
    }
}
/** Koniec, Tydzień 2, Wzorzec 1: Command, zastosowanie 2. */
