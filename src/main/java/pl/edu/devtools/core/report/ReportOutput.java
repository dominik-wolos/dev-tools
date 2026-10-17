package pl.edu.devtools.core.report;

import java.io.IOException;

@FunctionalInterface
public interface ReportOutput {
    void write(String text) throws IOException;
}
