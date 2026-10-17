package pl.edu.devtools.core.urls.observers;

/** Tydzień 2, Wzorzec 6: Observer, zastosowanie 1 — kontrakt odbiorcy zmian sesji. */
public interface UrlSessionObserver {
    void sessionChanged(UrlSessionObservable session);
}
