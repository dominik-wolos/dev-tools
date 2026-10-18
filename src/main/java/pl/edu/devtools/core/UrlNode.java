package pl.edu.devtools.core;

import java.net.URI;
import java.util.List;

/**
 * Tydzień 1, Wzorzec 5/9: Composite, zastosowanie 1.
 * Pojedynczy URL i zagnieżdżona grupa mają wspólny interfejs UrlNode.
 */
public interface UrlNode extends Iterable<URI> {
    List<URI> urls();

    UrlNode copy();
}
/** Koniec, Tydzień 1, Wzorzec 5/9: Composite, zastosowanie 1. */
