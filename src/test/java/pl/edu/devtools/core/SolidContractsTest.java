package pl.edu.devtools.core;

import org.junit.jupiter.api.Test;
import pl.edu.devtools.core.json.AbstractJsonTool;
import pl.edu.devtools.core.json.LoggingJacksonJsonTool;
import pl.edu.devtools.core.json.operations.Operation;
import pl.edu.devtools.core.processes.ProcessDetails;
import pl.edu.devtools.core.processes.ProcessIdentity;
import pl.edu.devtools.core.processes.ProcessListing;
import pl.edu.devtools.core.processes.ProcessStopping;
import pl.edu.devtools.core.report.Report;
import pl.edu.devtools.core.report.ReportExporter;
import pl.edu.devtools.core.report.TextReport;
import pl.edu.devtools.core.urls.UrlBuilder;
import pl.edu.devtools.core.urls.UrlCopyable;
import pl.edu.devtools.core.urls.UrlIterator;
import pl.edu.devtools.core.urls.UrlListing;
import pl.edu.devtools.core.urls.UrlSession;
import pl.edu.devtools.core.urls.observers.UrlSessionObservable;
import pl.edu.devtools.core.urls.observers.UrlSessionObservableSupport;
import pl.edu.devtools.core.urls.observers.UrlSessionObserver;
import pl.edu.devtools.core.urls.observers.UrlSessionState;
import pl.edu.devtools.core.urls.observers.UrlSessionSubscriptions;

import java.io.StringWriter;
import java.net.URI;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class SolidContractsTest {
    @Test
    void jsonImplementationWorksThroughAbstractBaseAndInterface() throws Exception {
        AbstractJsonTool base = new LoggingJacksonJsonTool();
        JsonTool tool = base;
        assertEquals("{\"id\":3}", tool.execute("{ \"id\": 3 }", Operation.MINIFY));
        assertThrows(IllegalArgumentException.class, () -> base.execute(" ", Operation.VALIDATE));
    }

    @Test
    void facadeExportsThroughInterfaceAndReportBase() throws Exception {
        var text = new StringWriter();
        Report base = new TextReport(text::write);
        ReportExporter exporter = base;
        var facade = facade();
        facade.export(List.of("one", "two"), exporter);
        assertEquals("one" + System.lineSeparator() + "two", text.toString());
        assertThrows(IllegalArgumentException.class, () -> facade.export(List.of(), exporter));
    }

    @Test
    void observableImplementationWorksThroughAbstractBaseAndSeparatedInterfaces() {
        var session = new UrlSession();
        UrlSessionObservableSupport base = session;
        UrlSessionObservable observable = base;
        UrlSessionSubscriptions subscriptions = observable;
        UrlSessionState state = observable;
        var changes = new AtomicInteger();
        UrlSessionObserver observer = updated -> {
            assertSame(state, updated);
            changes.incrementAndGet();
        };
        subscriptions.addObserver(observer);
        session.load(new UrlBuilder().template("localhost/{id}").parameters("1").build());
        assertEquals(1, state.batch().urls().size());
        subscriptions.removeObserver(observer);
        session.clear();
        assertEquals(1, changes.get());
    }

    @Test
    void processClientsUseSeparateCapabilitiesThroughFacade() {
        var processes = new RecordingProcesses();
        var facade = new DevToolsFacade(processes, new LoggingJacksonJsonTool(), uri -> {});
        ProcessListing listing = facade;
        ProcessStopping stopping = facade;
        assertEquals(List.of(), listing.list());
        assertTrue(stopping.stop(42));
        assertEquals(42, processes.stoppedPid());
    }

    @Test
    void urlIteratorNeedsOnlyIterationAndOpeningNeedsOnlyCopying() {
        URI address = URI.create("http://localhost/1");
        var iterator = new UrlIterator(List.of(List.of(address)).iterator());
        assertEquals(address, iterator.next());
        assertFalse(iterator.hasNext());
        UrlCopyable copyable = new UrlBuilder().template("localhost/{id}").parameters("1").build();
        UrlListing listing = copyable.copy();
        assertEquals(List.of(address), listing.urls());
        assertEquals(1, facade().open(copyable));
    }

    @Test
    void groupedProcessDataKeepsAllValuesAndAccessors() {
        var identity = new ProcessIdentity(42, "java", "owner");
        var details = new ProcessDetails(1.5, "/bin/java", "2026-11-07T10:00:00Z");
        var row = new ProcessRow(identity, details);
        assertEquals(42, row.pid());
        assertEquals("java", row.name());
        assertEquals("owner", row.user());
        assertEquals(1.5, row.cpuSeconds());
        assertEquals("/bin/java", row.command());
        assertEquals("2026-11-07T10:00:00Z", row.started());
    }

    private DevToolsFacade facade() {
        return new DevToolsFacade(new RecordingProcesses(), new LoggingJacksonJsonTool(), uri -> {});
    }
}
