package pl.edu.devtools.core.urls.states;

/**
 * Tydzień 2, Wzorzec 7: State, zastosowanie 1.
 * Kontrakt dostępności edycji i przejść cyklu otwierania URL-i.
 */
public interface UrlState {
    default boolean canEdit() {
        return true;
    }

    default void requireEditable() {
        if (!canEdit()) {
            throw new IllegalStateException("Trwa otwieranie adresów.");
        }
    }

    default UrlState beginOpening() {
        throw new IllegalStateException("Nie można teraz otworzyć adresów.");
    }

    default UrlState finishOpening() {
        return this;
    }
}
