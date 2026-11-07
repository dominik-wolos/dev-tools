package pl.edu.devtools.core.urls;

import pl.edu.devtools.core.UrlNode;

import java.net.URI;
import java.util.List;

/**
 * Lab 1, zadanie 1: Simple Factory, zastosowanie 1.
 * Na podstawie liczby adresów wybiera konkretny produkt: UrlLink albo UrlGroup.
 */
public class UrlNodeFactory {
    /**
     * Zachowuje kolejność i duplikaty; tworzy UrlLink dla jednego adresu, a UrlGroup dla kilku.
     * @throws IllegalArgumentException gdy lista adresów jest pusta
     */
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
/** Koniec, Lab 1, zadanie 1: Simple Factory, zastosowanie 1. */
