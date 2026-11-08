package pl.edu.devtools.core.urls;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Lab 2, zadanie 5: Memento, zastosowanie 4.
 * Opiekun przechowuje stos migawek bez dostępu do ich zawartości.
 */
final class UrlHistory {
    private final Deque<UrlMemento> snapshots = new ArrayDeque<>();

    void save(UrlMemento memento) {
        snapshots.push(memento);
    }

    UrlMemento previous() {
        return snapshots.peek();
    }

    void discardPrevious() {
        snapshots.pop();
    }

    void clear() {
        snapshots.clear();
    }
}
/** Koniec, Lab 2, zadanie 5: Memento, zastosowanie 4. */
