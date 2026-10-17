package pl.edu.devtools.core.json.operations;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Tydzień 2, Wzorzec 8: Strategy, zastosowanie 4.
 * Konkretna strategia formatuje JSON z wcięciami.
 */
public final class Format implements Operation {
    @Override
    public String apply(JsonNode value, ObjectMapper mapper) throws IOException {
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(value);
    }
}
/** Koniec, Tydzień 2, Wzorzec 8: Strategy, zastosowanie 4. */
