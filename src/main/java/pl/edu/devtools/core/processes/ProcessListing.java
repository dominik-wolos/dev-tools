package pl.edu.devtools.core.processes;

import pl.edu.devtools.core.ProcessRow;
import java.util.List;

/** Lab 3, zadanie 3: ISP, zastosowanie 2 — odczyt procesów nie wymaga możliwości ich zatrzymania. */
public interface ProcessListing {
    List<ProcessRow> list();
}
