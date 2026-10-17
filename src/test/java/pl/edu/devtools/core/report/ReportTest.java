package pl.edu.devtools.core.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pl.edu.devtools.infrastructure.FileReportOutput;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class ReportTest {
    @Test
    void missingDataDoesNotOverwriteExistingFile(@TempDir Path folder) throws IOException {
        Path file = folder.resolve("report.txt");
        Files.writeString(file, "original");
        var output = new FileReportOutput(file);
        for (Report report : List.of(new TextReport(output), new JsonReport(output, new ObjectMapper()))) {
            assertThrows(IllegalArgumentException.class, () -> report.export(null));
            assertThrows(IllegalArgumentException.class, () -> report.export(List.of()));
            assertEquals("original", Files.readString(file));
        }
    }

    @Test
    void validationAndRenderingFailuresLeaveDestinationUntouched() {
        var destination = new AtomicReference<>("original");
        var report = new FailingReport(destination::set);
        assertThrows(IllegalArgumentException.class, () -> report.export(List.of()));
        var error = assertThrows(IOException.class, () -> report.export(List.of("value")));
        assertEquals("render failed", error.getMessage());
        assertEquals("original", destination.get());
    }
}
