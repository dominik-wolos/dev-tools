package pl.edu.devtools.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pl.edu.devtools.infrastructure.FileReportOutput;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class DevToolsTest {
    private final JsonTool json = new JsonTool.Jackson();

    @Test
    void validatesObjectsArraysAndScalarValues() throws IOException {
        for (String input : List.of("{\"a\":1}", "[1,true,null]", "null", "42", "\"text\"")) {
            assertEquals("JSON jest poprawny.", json.execute(input, false));
        }
    }

    @Test
    void rejectsEmptyInputAndInvalidSyntax() {
        assertThrows(IllegalArgumentException.class, () -> json.execute("  ", false));
        assertThrows(IllegalArgumentException.class, () -> json.execute(null, false));
        assertThrows(JsonProcessingException.class, () -> json.execute("{bad}", false));
        assertThrows(JsonProcessingException.class, () -> json.execute("{\"a\":1,}", true));
    }

    @Test
    void rejectsTrailingDocumentAndGarbage() {
        assertThrows(JsonProcessingException.class, () -> json.execute("{} {}", false));
        assertThrows(JsonProcessingException.class, () -> json.execute("[] extra", true));
    }

    @Test
    void formattingPreservesPreciseNumbersAndProducesIndentedJson() throws IOException {
        String formatted = json.execute("{\"value\":0.12345678901234567890123456789,\"large\":12345678901234567890123456789}", true);
        assertTrue(formatted.contains("\n"));
        assertTrue(formatted.contains("0.12345678901234567890123456789"));
        assertTrue(formatted.contains("12345678901234567890123456789"));
    }

    @Test
    void decoratorPreservesResultAndException() throws IOException {
        var measured = new JsonTool.Timed(json);
        assertEquals(json.execute("{}", true), measured.execute("{}", true));
        assertTrue(measured.elapsedMillis() >= 0);
        assertThrows(JsonProcessingException.class, () -> measured.execute("{", true));
        assertTrue(measured.elapsedMillis() >= 0);
    }

    @Test
    void supportsMixedSeparatorsAndPreservesOrderLeadingZerosAndDuplicates() {
        var batch = new UrlNode.Builder().template("localhost/orders/{orderID}")
                .parameters("1, 2;003 | 4\t5\n6abc7/7").build();
        assertEquals(List.of("1", "2", "003", "4", "5", "6", "7", "7").stream()
                .map(id -> URI.create("http://localhost/orders/" + id)).toList(), batch.urls());
    }

    @Test
    void supportsPortsQueryParametersAndRepeatedPlaceholder() {
        var batch = new UrlNode.Builder().template("https://localhost:8443/orders/{id}?same={id}")
                .parameters("42").build();
        assertEquals(List.of(URI.create("https://localhost:8443/orders/42?same=42")), batch.urls());
    }

    @Test
    void supportsAnyLengthNumericIdentifier() {
        String id = "123456789012345678901234567890";
        var batch = new UrlNode.Builder().template("localhost/{id}").parameters(id).build();
        assertEquals("http://localhost/" + id, batch.urls().get(0).toString());
    }

    @Test
    void rejectsMissingInputsAndAmbiguousTemplates() {
        for (String template : List.of("", "localhost/orders", "localhost/{a}/{b}", "localhost/{bad-name}")) {
            assertThrows(IllegalArgumentException.class,
                    () -> new UrlNode.Builder().template(template).parameters("1").build());
        }
        assertThrows(IllegalArgumentException.class,
                () -> new UrlNode.Builder().template("localhost/{id}").parameters("abc, ;").build());
    }

    @Test
    void rejectsInvalidHostsUnsupportedSchemesAndCredentials() {
        for (String template : List.of("file:///tmp/{id}", "ftp://host/{id}", "https:///{id}",
                "http://bad host/{id}", "http://host:99999/{id}", "http://user:pass@host/{id}")) {
            assertThrows(IllegalArgumentException.class,
                    () -> new UrlNode.Builder().template(template).parameters("1").build(), template);
        }
    }

    @Test
    void factoryChoosesConcreteProductByInput() {
        URI first = URI.create("http://localhost/1");
        URI second = URI.create("http://localhost/2");
        assertInstanceOf(UrlNode.Link.class, UrlNode.create("one", List.of(first)));
        assertInstanceOf(UrlNode.Group.class, UrlNode.create("two", List.of(first, second)));
        assertThrows(IllegalArgumentException.class, () -> UrlNode.create("empty", List.of()));
    }

    @Test
    void compositeRecursivelyTraversesGroups() {
        var batch = new UrlNode.Builder().template("localhost/{id}").parameters("1,2\n3,4").build();
        assertEquals(2, batch.children().size());
        assertInstanceOf(UrlNode.Group.class, batch.children().get(0));
        assertEquals(4, batch.urls().size());
    }

    @Test
    void prototypeCreatesIndependentNestedChildren() {
        var original = new UrlNode.Builder().template("localhost/{id}").parameters("1,2\n3").build();
        var copy = original.copy();
        var nestedCopy = (UrlNode.Group) copy.children().get(0);
        assertNotSame(original.children().get(0), nestedCopy);
        nestedCopy.remove(nestedCopy.children().get(0));
        assertEquals(2, copy.urls().size());
        assertEquals(3, original.urls().size());
        assertEquals(3, original.copy().urls().size());
    }

    @Test
    void compositeRejectsDirectAndIndirectCycles() {
        var parent = new UrlNode.Group("parent");
        var child = new UrlNode.Group("child");
        parent.add(child);
        assertThrows(IllegalArgumentException.class, () -> parent.add(parent));
        assertThrows(IllegalArgumentException.class, () -> child.add(parent));
    }

    @Test
    void proxyBlocksOwnAndSystemPidAndDelegatesOtherRequests() {
        AtomicLong stopped = new AtomicLong(-1);
        Processes underlying = new Processes() {
            public List<ProcessRow> list() { return List.of(); }
            public boolean stop(long pid) { stopped.set(pid); return true; }
        };
        var proxy = new Processes.Guarded(underlying, 123);
        for (long pid : new long[]{-1, 0, 1, 123}) {
            assertThrows(IllegalArgumentException.class, () -> proxy.stop(pid));
        }
        assertEquals(-1, stopped.get());
        assertTrue(proxy.stop(456));
        assertEquals(456, stopped.get());
        assertTrue(proxy.list().isEmpty());
    }

    @Test
    void systemAdapterListsCurrentJvmWithoutStoppingAnything() {
        var rows = new Processes.SystemProcesses().list();
        assertTrue(rows.stream().anyMatch(row -> row.pid() == ProcessHandle.current().pid()));
        assertTrue(rows.stream().allMatch(row -> row.pid() > 0 && row.name() != null));
    }

    @Test
    void bridgeSupportsBothFormatsWithBothDestinations(@TempDir Path folder) throws IOException {
        List<String> lines = List.of("http://localhost/1", "http://localhost/2");
        var facade = facade(new ArrayList<>());
        for (boolean asJson : new boolean[]{false, true}) {
            AtomicReference<String> clipboard = new AtomicReference<>();
            Path file = folder.resolve(asJson ? "result.json" : "result.txt");
            facade.export(lines, asJson, clipboard::set);
            facade.export(lines, asJson, new FileReportOutput(file));
            assertEquals(clipboard.get(), Files.readString(file));
            if (asJson) {
                assertEquals(lines, new ObjectMapper().readValue(Files.readString(file), List.class));
            } else {
                assertEquals(String.join(System.lineSeparator(), lines), Files.readString(file));
            }
        }
    }

    @Test
    void bridgePropagatesDestinationFailure() {
        assertThrows(IOException.class, () -> new Report.Text(text -> {
            throw new IOException("destination failed");
        }).export(List.of("test")));
    }

    @Test
    void facadeBuildsAndOpensOnlyRemainingUrlsThroughInjectedBrowser() {
        List<URI> opened = new ArrayList<>();
        var facade = facade(opened);
        var batch = facade.urls("localhost/orders/{orderID}", "1,2\n3");
        batch.remove(batch.children().get(1));
        assertEquals(2, facade.open(batch));
        assertEquals(List.of(URI.create("http://localhost/orders/1"), URI.create("http://localhost/orders/2")), opened);
    }

    @Test
    void facadeReportsPartialBrowserFailure() {
        var facade = new DevToolsFacade(new Processes.SystemProcesses(), json, uri -> {
            if (uri.getPath().endsWith("2")) { throw new IllegalStateException("browser failed"); }
        });
        var batch = facade.urls("localhost/{id}", "1,2,3");
        var error = assertThrows(IllegalStateException.class, () -> facade.open(batch));
        assertTrue(error.getMessage().contains("Otwarto 1 z 3"));
    }

    private DevToolsFacade facade(List<URI> opened) {
        return new DevToolsFacade(new Processes.SystemProcesses(), json, opened::add);
    }
}
