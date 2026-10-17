package pl.edu.devtools.core;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import pl.edu.devtools.core.json.operations.Operation;

import java.io.IOException;

public interface JsonTool {
    String execute(String input, Operation operation) throws IOException;

    final class Jackson implements JsonTool {
        private final ObjectMapper mapper = new ObjectMapper()
                .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
                .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
                .enable(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS);

        @Override
        public String execute(String input, Operation operation) throws IOException {
            if (input == null || input.isBlank()) {
                throw new IllegalArgumentException("Wpisz JSON do sprawdzenia.");
            }
            JsonNode value = mapper.readTree(input);
            if (value == null || value.isMissingNode()) {
                throw new IllegalArgumentException("Wpisz JSON do sprawdzenia.");
            }
            /**
             * Tydzień 2, Wzorzec 8: Strategy, zastosowanie 2.
             * Po wspólnej walidacji następuje wywołanie wybranej strategii.
             */
            return operation.apply(value, mapper);
            /** Koniec, Tydzień 2, Wzorzec 8: Strategy, zastosowanie 2. */
        }
    }

    /**
     * Tydzień 1, Wzorzec 6/9: Decorator, zastosowanie 1.
     * Opakowanie zachowuje interfejs narzędzia i mierzy czas także przy błędzie JSON-a.
     */
    final class Timed implements JsonTool {
        private final JsonTool delegate;
        private long elapsedNanos;

        public Timed(JsonTool delegate) {
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
    /** Koniec, Tydzień 1, Wzorzec 6/9: Decorator, zastosowanie 1.*/
}
