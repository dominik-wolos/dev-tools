package pl.edu.devtools.core.urls.states;

/** Gotowy podgląd pozwala rozpocząć otwieranie. */
public final class ReadyState implements UrlState {
    @Override
    public UrlState beginOpening() {
        return new OpeningState();
    }
}
