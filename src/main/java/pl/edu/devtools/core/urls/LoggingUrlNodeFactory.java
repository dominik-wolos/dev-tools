package pl.edu.devtools.core.urls;

import pl.edu.devtools.core.UrlNode;

import java.net.URI;
import java.util.List;
import java.util.logging.Logger;

/**
 * Lab 3, zadanie 1: Liskov, zastosowanie 2.
 * Dodaje diagnostykę do UrlNodeFactory bez zmiany rodzaju węzła, kolejności i duplikatów.
 */
public final class LoggingUrlNodeFactory extends UrlNodeFactory {
    private static final Logger LOGGER = Logger.getLogger(LoggingUrlNodeFactory.class.getName());

    @Override
    public UrlNode create(String label, List<URI> addresses) {
        UrlNode node = super.create(label, addresses);
        LOGGER.fine(() -> "Utworzono węzeł URL, adresów: " + addresses.size());
        return node;
    }
}
/** Koniec, Lab 3, zadanie 1: Liskov, zastosowanie 2. */
