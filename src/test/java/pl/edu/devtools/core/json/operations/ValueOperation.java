package pl.edu.devtools.core.json.operations;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class ValueOperation implements Operation {
    @Override
    public String apply(JsonNode value, ObjectMapper mapper) {
        return value.get("value").asText();
    }
}
