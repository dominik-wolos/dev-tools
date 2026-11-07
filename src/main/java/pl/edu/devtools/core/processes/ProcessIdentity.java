package pl.edu.devtools.core.processes;

/** Lab 3, zadanie 8: Maksymalnie 3 argumenty, zastosowanie 2 — dane identyfikujące proces. */
public record ProcessIdentity(long pid, String name, String user) {
}
