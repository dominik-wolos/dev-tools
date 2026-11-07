package pl.edu.devtools.core.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import pl.edu.devtools.core.json.operations.Operation;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static pl.edu.devtools.core.json.operations.Operation.*;

class JacksonJsonToolSubstitutionTest {
    @Test
    void baseAndDerivedObjectsProduceTheSameResultsThroughTheBaseType() throws IOException {
        Operation custom = (value, mapper) -> "Wynik: " + mapper.writeValueAsString(value);
        for (String input : List.of("{\"value\":0.12345678901234567890123456789}",
                "[true,null,\"Zażółć\"]", "42", "null")) {
            for (Operation operation : List.of(VALIDATE, FORMAT, MINIFY, custom)) {
                String expected = new JacksonJsonTool().execute(input, operation);
                for (JacksonJsonTool tool : tools()) {
                    assertEquals(expected, tool.execute(input, operation));
                }
            }
        }
    }

    @Test
    void baseAndDerivedObjectsRejectTheSameInvalidInputs() {
        for (JacksonJsonTool tool : tools()) {
            for (Operation operation : List.of(VALIDATE, FORMAT, MINIFY)) {
                assertThrows(IllegalArgumentException.class, () -> tool.execute(null, operation));
                assertThrows(IllegalArgumentException.class, () -> tool.execute("  ", operation));
                for (String input : List.of("{bad}", "{} {}", "[] extra")) {
                    assertThrows(JsonProcessingException.class, () -> tool.execute(input, operation));
                }
            }
        }
    }

    @Test
    void baseAndDerivedObjectsPropagateTheOriginalOperationFailure() {
        var failure = new IOException("operation failed");
        Operation operation = (value, mapper) -> { throw failure; };
        for (JacksonJsonTool tool : tools()) {
            assertSame(failure, assertThrows(IOException.class, () -> tool.execute("{}", operation)));
        }
    }

    private List<JacksonJsonTool> tools() {
        return List.of(new JacksonJsonTool(), new LoggingJacksonJsonTool());
    }
}
