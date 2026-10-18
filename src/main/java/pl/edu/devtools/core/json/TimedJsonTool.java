package pl.edu.devtools.core.json;

import pl.edu.devtools.core.JsonTool;
import pl.edu.devtools.core.json.operations.Operation;

import java.io.IOException;

/**
 * Tydzień 1, Wzorzec 6/9: Decorator, zastosowanie 1.
 * Opakowanie zachowuje interfejs narzędzia i mierzy czas także przy błędzie JSON-a.
 */
public final class TimedJsonTool implements JsonTool {
    private final JsonTool delegate;
    private long elapsedNanos;

    public TimedJsonTool(JsonTool delegate) {
        this.delegate = delegate;
    }

    @Override
    public String execute(String input, Operation operation) throws IOException {
        long start = System.nanoTime();
        try {
            return delegate.execute(input, operation);
        } finally {
            elapsedNanos = System.nanoTime() - start;
        }
    }

    public double elapsedMillis() {
        return elapsedNanos / 1_000_000.0;
    }
}
/** Koniec, Tydzień 1, Wzorzec 6/9: Decorator, zastosowanie 1. */
