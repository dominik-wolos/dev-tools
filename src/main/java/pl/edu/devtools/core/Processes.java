package pl.edu.devtools.core;

import pl.edu.devtools.core.processes.ProcessListing;
import pl.edu.devtools.core.processes.ProcessStopping;

/** Tydzień 3, punkt 3: ISP — zbiorczy kontrakt adaptera i proxy, klienci wybierają węższe role. */
public interface Processes extends ProcessListing, ProcessStopping {
}
