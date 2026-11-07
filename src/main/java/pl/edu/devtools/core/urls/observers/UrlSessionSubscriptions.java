package pl.edu.devtools.core.urls.observers;

/** Tydzień 3, punkt 3: ISP — rejestracja odbiorców bez odczytu danych sesji. */
public interface UrlSessionSubscriptions {
    void addObserver(UrlSessionObserver observer);

    void removeObserver(UrlSessionObserver observer);
}
