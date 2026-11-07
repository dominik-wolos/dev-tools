package pl.edu.devtools.core.urls.observers;

/** Lab 3, zadanie 3: ISP, zastosowanie 7 — rejestracja odbiorców bez odczytu danych sesji. */
public interface UrlSessionSubscriptions {
    void addObserver(UrlSessionObserver observer);

    void removeObserver(UrlSessionObserver observer);
}
