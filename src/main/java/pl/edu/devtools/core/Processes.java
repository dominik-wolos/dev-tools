package pl.edu.devtools.core;

import pl.edu.devtools.core.processes.ProcessListing;
import pl.edu.devtools.core.processes.ProcessStopping;

/** Lab 3, zadanie 3: ISP, zastosowanie 1 — zbiorczy kontrakt adaptera i proxy, klienci wybierają węższe role. */
public interface Processes extends ProcessListing, ProcessStopping {
}
