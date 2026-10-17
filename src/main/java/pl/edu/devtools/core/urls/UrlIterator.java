package pl.edu.devtools.core.urls;

import pl.edu.devtools.core.UrlNode;

import java.net.URI;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Tydzień 2, Wzorzec 3: Iterator, zastosowanie 1.
 * Leniwe przejście DFS korzysta z iteratorów dzieci bez spłaszczania drzewa.
 */
public final class UrlIterator implements Iterator<URI> {
    private final Iterator<? extends UrlNode> nodes;
    private Iterator<URI> current = Collections.emptyIterator();

    public UrlIterator(Iterator<? extends UrlNode> nodes) {
        this.nodes = Objects.requireNonNull(nodes);
    }

    @Override
    public boolean hasNext() {
        while (!current.hasNext() && nodes.hasNext()) {
            current = nodes.next().iterator();
        }
        return current.hasNext();
    }

    @Override
    public URI next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return current.next();
    }
}
/** Koniec, Tydzień 2, Wzorzec 3: Iterator, zastosowanie 1. */
