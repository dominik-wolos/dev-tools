package pl.edu.devtools.core.json;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import pl.edu.devtools.core.JsonTool;
import pl.edu.devtools.core.json.operations.Operation;

import java.io.IOException;

public final class JacksonJsonTool implements JsonTool {
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
