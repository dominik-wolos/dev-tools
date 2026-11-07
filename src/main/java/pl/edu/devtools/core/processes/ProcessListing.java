package pl.edu.devtools.core.processes;

import pl.edu.devtools.core.ProcessRow;
import java.util.List;

/** Tydzień 3, punkt 3: ISP — odczyt procesów nie wymaga możliwości ich zatrzymania. */
public interface ProcessListing {
    List<ProcessRow> list();
}
