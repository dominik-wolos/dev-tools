package pl.edu.devtools.core.urls;

import pl.edu.devtools.core.UrlNode;

import java.net.URI;
import java.util.Iterator;
import java.util.List;

final class CountingUrlNode implements UrlNode {
    private final URI uri;
    int iteratorCalls;
    int urlsCalls;

    CountingUrlNode(URI uri) {
        this.uri = uri;
    }

    @Override
    public Iterator<URI> iterator() {
        iteratorCalls++;
        return List.of(uri).iterator();
    }

    @Override
    public List<URI> urls() {
        urlsCalls++;
        return List.of(uri);
    }

    @Override
    public UrlNode copy() {
        return new CountingUrlNode(uri);
    }
}
