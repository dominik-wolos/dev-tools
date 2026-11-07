package pl.edu.devtools.core.urls.observers;

/** Lab 2, zadanie 6: Observer, zastosowanie 1 — kontrakt odbiorcy zmian sesji. */
public interface UrlSessionObserver {
    void sessionChanged(UrlSessionState session);
}
