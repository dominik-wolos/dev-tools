package pl.edu.devtools.core.urls.observers;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/** Wspólna implementacja obserwowalności; klasa abstrakcyjna zastępuje trait w Javie. */
public abstract class UrlSessionObservableSupport implements UrlSessionObservable {
    private final Set<UrlSessionObserver> observers = new LinkedHashSet<>();

    /** Tydzień 2, Wzorzec 6: Observer, zastosowanie 2 — rejestracja i odpinanie odbiorców. */
    @Override
    public final void addObserver(UrlSessionObserver observer) {
        observers.add(Objects.requireNonNull(observer));
    }

    @Override
    public final void removeObserver(UrlSessionObserver observer) {
        observers.remove(observer);
    }

    /**
     * Tydzień 2, Wzorzec 6: Observer, zastosowanie 3.
     * Synchroniczne wywołanie odbiorców po zmianie; można odpiąć się podczas powiadomienia.
     */
    protected final void notifyObservers() {
        for (UrlSessionObserver observer : List.copyOf(observers)) {
            observer.sessionChanged(this);
        }
    }
}
