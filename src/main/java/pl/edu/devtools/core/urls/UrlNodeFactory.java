package pl.edu.devtools.core.urls;

import pl.edu.devtools.core.UrlNode;

import java.net.URI;
import java.util.List;

/**
 * Tydzień 1, Wzorzec 1/9: Simple Factory, zastosowanie 1.
 * Na podstawie liczby adresów wybiera konkretny produkt: UrlLink albo UrlGroup.
 */
public final class UrlNodeFactory {
    public UrlNode create(String label, List<URI> addresses) {
        if (addresses.isEmpty()) {
            throw new IllegalArgumentException("Lista adresów jest pusta.");
        }
        if (addresses.size() == 1) {
            return new UrlLink(addresses.get(0));
        }
        UrlGroup group = new UrlGroup(label);
        addresses.forEach(uri -> group.add(new UrlLink(uri)));
        return group;
    }
}
/** Koniec, Tydzień 1, Wzorzec 1/9: Simple Factory, zastosowanie 1. */
