package pl.edu.devtools.core;

import java.io.IOException;
import java.net.URI;

@FunctionalInterface
public interface Browser {
    void open(URI uri) throws IOException;
}
