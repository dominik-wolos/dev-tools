package pl.edu.devtools.core.processes;

/** Tydzień 3, punkt 3: ISP — osobna operacja zatrzymania procesu. */
public interface ProcessStopping {
    boolean stop(long pid);
}
