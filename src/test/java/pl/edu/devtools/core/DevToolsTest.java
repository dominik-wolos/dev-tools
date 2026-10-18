package pl.edu.devtools.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pl.edu.devtools.core.json.JacksonJsonTool;
import pl.edu.devtools.core.json.TimedJsonTool;
import pl.edu.devtools.core.json.operations.ValueOperation;
import pl.edu.devtools.core.processes.GuardedProcesses;
import pl.edu.devtools.core.report.JsonReport;
import pl.edu.devtools.core.report.Report;
import pl.edu.devtools.core.report.ReportOutput;
import pl.edu.devtools.core.report.TextReport;
import pl.edu.devtools.core.urls.UrlBuilder;
import pl.edu.devtools.core.urls.UrlGroup;
import pl.edu.devtools.core.urls.UrlLink;
import pl.edu.devtools.core.urls.UrlNodeFactory;
import pl.edu.devtools.infrastructure.FileReportOutput;
import pl.edu.devtools.infrastructure.SystemProcesses;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static pl.edu.devtools.core.json.operations.Operation.*;

class DevToolsTest {
    private final JsonTool json = new JacksonJsonTool();

    @Test
    void validatesObjectsArraysAndScalarValues() throws IOException {
        for (String input : List.of("{\"a\":1}", "[1,true,null]", "null", "42", "\"text\"")) {
            assertEquals("JSON jest poprawny.", json.execute(input, VALIDATE));
        }
    }

    @Test
    void rejectsEmptyInputAndInvalidSyntax() {
        for (var operation : List.of(VALIDATE, FORMAT, MINIFY)) {
            assertThrows(IllegalArgumentException.class, () -> json.execute("  ", operation));
            assertThrows(IllegalArgumentException.class, () -> json.execute(null, operation));
            assertThrows(JsonProcessingException.class, () -> json.execute("{bad}", operation));
            assertThrows(JsonProcessingException.class, () -> json.execute("{\"a\":1,}", operation));
        }
    }

    @Test
    void rejectsTrailingDocumentAndGarbage() {
        for (var operation : List.of(VALIDATE, FORMAT, MINIFY)) {
            assertThrows(JsonProcessingException.class, () -> json.execute("{} {}", operation));
            assertThrows(JsonProcessingException.class, () -> json.execute("[] extra", operation));
        }
    }

    @Test
    void formattingPreservesPreciseNumbersAndProducesIndentedJson() throws IOException {
        String formatted = json.execute("{\"value\":0.12345678901234567890123456789,\"large\":12345678901234567890123456789}", FORMAT);
        assertTrue(formatted.contains("\n"));
        assertTrue(formatted.contains("0.12345678901234567890123456789"));
        assertTrue(formatted.contains("12345678901234567890123456789"));
    }

    @Test
    void minificationPreservesStringsAndPreciseNumbers() throws IOException {
        String compact = "{\"text\":\" a b \",\"value\":0.12345678901234567890123456789,\"large\":12345678901234567890123456789,\"items\":[true,null]}";
        assertEquals(compact, json.execute(json.execute(compact, FORMAT), MINIFY));
        assertEquals("null", json.execute(" null ", MINIFY));
    }

    @Test
    void facadeAcceptsNewStrategyWithoutChangingParserOrDecorator() throws IOException {
        var facade = facade(new ArrayList<>());
        var operation = new ValueOperation();
        assertEquals("42", facade.json("{\"value\":42}", operation));
        assertEquals("JSON jest poprawny.", facade.json("{}", VALIDATE));
        assertThrows(JsonProcessingException.class, () -> facade.json("{} {}", operation));
    }

    @Test
    void decoratorPreservesResultAndException() throws IOException {
        var measured = new TimedJsonTool(json);
        assertEquals(json.execute("{}", FORMAT), measured.execute("{}", FORMAT));
        assertTrue(measured.elapsedMillis() >= 0);
        assertThrows(JsonProcessingException.class, () -> measured.execute("{", FORMAT));
        assertTrue(measured.elapsedMillis() >= 0);
    }

    @Test
    void supportsMixedSeparatorsAndPreservesOrderLeadingZerosAndDuplicates() {
        var batch = new UrlBuilder().template("localhost/orders/{orderID}")
                .parameters("1, 2;003 | 4\t5\n6abc7/7").build();
        assertEquals(List.of("1", "2", "003", "4", "5", "6", "7", "7").stream()
                .map(id -> URI.create("http://localhost/orders/" + id)).toList(), batch.urls());
    }

    @Test
    void supportsPortsQueryParametersAndRepeatedPlaceholder() {
        var batch = new UrlBuilder().template("https://localhost:8443/orders/{id}?same={id}")
                .parameters("42").build();
        assertEquals(List.of(URI.create("https://localhost:8443/orders/42?same=42")), batch.urls());
    }

    @Test
    void supportsAnyLengthNumericIdentifier() {
        String id = "123456789012345678901234567890";
        var batch = new UrlBuilder().template("localhost/{id}").parameters(id).build();
        assertEquals("http://localhost/" + id, batch.urls().get(0).toString());
    }

    @Test
    void rejectsMissingInputsAndAmbiguousTemplates() {
        for (String template : List.of("", "localhost/orders", "localhost/{a}/{b}", "localhost/{bad-name}")) {
            assertThrows(IllegalArgumentException.class,
                    () -> new UrlBuilder().template(template).parameters("1").build());
        }
        assertThrows(IllegalArgumentException.class,
                () -> new UrlBuilder().template("localhost/{id}").parameters("abc, ;").build());
    }

    @Test
    void rejectsInvalidHostsUnsupportedSchemesAndCredentials() {
        for (String template : List.of("file:///tmp/{id}", "ftp://host/{id}", "https:///{id}",
                "http://bad host/{id}", "http://host:99999/{id}", "http://user:pass@host/{id}")) {
            assertThrows(IllegalArgumentException.class,
                    () -> new UrlBuilder().template(template).parameters("1").build(), template);
        }
    }

    @Test
    void factoryChoosesConcreteProductByInput() {
        URI first = URI.create("http://localhost/1");
        URI second = URI.create("http://localhost/2");
        assertInstanceOf(UrlLink.class, new UrlNodeFactory().create("one", List.of(first)));
        assertInstanceOf(UrlGroup.class, new UrlNodeFactory().create("two", List.of(first, second)));
        assertThrows(IllegalArgumentException.class, () -> new UrlNodeFactory().create("empty", List.of()));
    }

    @Test
    void compositeRecursivelyTraversesGroups() {
        var batch = new UrlBuilder().template("localhost/{id}").parameters("1,2\n3,4").build();
        assertEquals(2, batch.children().size());
        assertInstanceOf(UrlGroup.class, batch.children().get(0));
        assertEquals(4, batch.urls().size());
    }

    @Test
    void prototypeCreatesIndependentNestedChildren() {
        var original = new UrlBuilder().template("localhost/{id}").parameters("1,2\n3").build();
        var copy = original.copy();
        var nestedCopy = (UrlGroup) copy.children().get(0);
        assertNotSame(original.children().get(0), nestedCopy);
        nestedCopy.remove(nestedCopy.children().get(0));
        assertEquals(2, copy.urls().size());
        assertEquals(3, original.urls().size());
        assertEquals(3, original.copy().urls().size());
    }

    @Test
    void compositeRejectsDirectAndIndirectCycles() {
        var parent = new UrlGroup("parent");
        var child = new UrlGroup("child");
        parent.add(child);
        assertThrows(IllegalArgumentException.class, () -> parent.add(parent));
        assertThrows(IllegalArgumentException.class, () -> child.add(parent));
    }

    @Test
    void proxyBlocksOwnAndSystemPidAndDelegatesOtherRequests() {
        var underlying = new RecordingProcesses();
        var proxy = new GuardedProcesses(underlying, 123);
        for (long pid : new long[]{-1, 0, 1, 123}) {
            assertThrows(IllegalArgumentException.class, () -> proxy.stop(pid));
        }
        assertEquals(-1, underlying.stoppedPid());
        assertTrue(proxy.stop(456));
        assertEquals(456, underlying.stoppedPid());
        assertTrue(proxy.list().isEmpty());
    }

    @Test
    void systemAdapterListsCurrentJvmWithoutStoppingAnything() {
        var rows = new SystemProcesses().list();
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

            ReportOutput memory = clipboard::set;
            for (ReportOutput destination : List.of(memory, new FileReportOutput(file))) {
                Report report = asJson ? new JsonReport(destination, new ObjectMapper()) : new TextReport(destination);
                facade.export(lines, report);
            }
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
        assertThrows(IOException.class, () -> new TextReport(text -> {
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
        List<URI> attempted = new ArrayList<>();
        var facade = new DevToolsFacade(new SystemProcesses(), json, uri -> {
            attempted.add(uri);
            if (uri.getPath().endsWith("2")) { throw new IllegalStateException("browser failed"); }
        });
        var batch = facade.urls("localhost/{id}", "1,2,3");
        var error = assertThrows(IllegalStateException.class, () -> facade.open(batch));
        assertTrue(error.getMessage().contains("Otwarto 1 z 3"));
        assertEquals(List.of(URI.create("http://localhost/1"), URI.create("http://localhost/2")), attempted);
    }

    private DevToolsFacade facade(List<URI> opened) {
        return new DevToolsFacade(new SystemProcesses(), json, opened::add);
    }
}
