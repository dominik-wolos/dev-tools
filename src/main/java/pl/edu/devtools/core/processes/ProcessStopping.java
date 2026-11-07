package pl.edu.devtools.core.processes;

/** Lab 3, zadanie 3: ISP, zastosowanie 3 — osobna operacja zatrzymania procesu. */
public interface ProcessStopping {
    boolean stop(long pid);
}
