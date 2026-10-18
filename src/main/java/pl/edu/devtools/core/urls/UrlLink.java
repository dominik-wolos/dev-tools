package pl.edu.devtools.core.urls;

import pl.edu.devtools.core.UrlNode;

import java.net.URI;
import java.util.Iterator;
import java.util.List;

/**
 * Tydzień 1, Wzorzec 5/9: Composite, zastosowanie 1.
 * Pojedynczy URL i zagnieżdżona grupa mają wspólny interfejs UrlNode.
 */
public record UrlLink(URI uri) implements UrlNode {
    @Override
    public Iterator<URI> iterator() {
        return List.of(uri).iterator();
    }

    @Override
    public List<URI> urls() {
        return List.of(uri);
    }

    @Override
    public UrlLink copy() {
        return new UrlLink(uri);
    }

    @Override
    public String toString() {
        return uri.toString();
    }
}
/** Koniec, Tydzień 1, Wzorzec 5/9: Composite, zastosowanie 1. */
