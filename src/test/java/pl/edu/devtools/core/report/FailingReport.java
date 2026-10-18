package pl.edu.devtools.core.report;

import java.io.IOException;
import java.util.List;

final class FailingReport extends Report {
    FailingReport(ReportOutput output) {
        super(output);
    }

    @Override
    protected String render(List<String> lines) throws IOException {
        throw new IOException("render failed");
    }
}
