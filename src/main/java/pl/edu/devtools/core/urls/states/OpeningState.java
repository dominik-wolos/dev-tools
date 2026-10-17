package pl.edu.devtools.core.urls.states;

/** Otwieranie blokuje edycję; zakończenie zadania przywraca gotowość. */
public final class OpeningState implements UrlState {
    @Override
    public boolean canEdit() {
        return false;
    }

    @Override
    public UrlState finishOpening() {
        return new ReadyState();
    }
}
