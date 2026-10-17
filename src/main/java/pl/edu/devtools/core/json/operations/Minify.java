package pl.edu.devtools.core.json.operations;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Tydzień 2, Wzorzec 8: Strategy, zastosowanie 5.
 * Konkretna strategia zapisuje JSON bez zbędnych odstępów i wcięć.
 */
public final class Minify implements Operation {
    @Override
    public String apply(JsonNode value, ObjectMapper mapper) throws IOException {
        return mapper.writeValueAsString(value);
    }
}
/** Koniec, Tydzień 2, Wzorzec 8: Strategy, zastosowanie 5. */
