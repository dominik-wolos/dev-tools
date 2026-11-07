package pl.edu.devtools.infrastructure;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileReportOutputSubstitutionTest {
    @Test
    void baseAndDerivedObjectsWriteUtf8AndReplaceContentsThroughTheBaseType(@TempDir Path folder)
            throws IOException {
        Path file = folder.resolve("report.txt");
        String text = "Zażółć gęślą jaźń\nURL: https://localhost/003";
        for (FileReportOutput output : outputs(file)) {
            Files.writeString(file, "Poprzednia zawartość, która ma zostać całkowicie zastąpiona.");
            output.write(text);
            assertArrayEquals(text.getBytes(StandardCharsets.UTF_8), Files.readAllBytes(file));
            output.write("Nowy raport");
            assertEquals("Nowy raport", Files.readString(file));
        }
    }

    @Test
    void baseAndDerivedObjectsAcceptEmptyText(@TempDir Path folder) throws IOException {
        Path file = folder.resolve("empty.txt");
        for (FileReportOutput output : outputs(file)) {
            Files.writeString(file, "Poprzednia zawartość");
            output.write("");
            assertEquals(0, Files.size(file));
        }
    }

    @Test
    void baseAndDerivedObjectsPropagateTheSameWriteFailure(@TempDir Path folder) {
        IOException expected = assertThrows(IOException.class, () -> new FileReportOutput(folder).write("raport"));
        for (FileReportOutput output : outputs(folder)) {
            IOException actual = assertThrows(IOException.class, () -> output.write("raport"));
            assertEquals(expected.getClass(), actual.getClass());
            assertEquals(expected.getMessage(), actual.getMessage());
        }
    }

    private List<FileReportOutput> outputs(Path file) {
        return List.of(new FileReportOutput(file), new LoggingFileReportOutput(file));
    }
}
