package pl.edu.devtools.core.processes;

/** Lab 3, zadanie 8: Maksymalnie 3 argumenty, zastosowanie 3 — dane wykonania procesu. */
public record ProcessDetails(Double cpuSeconds, String command, String started) {
}
