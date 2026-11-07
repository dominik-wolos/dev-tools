package pl.edu.devtools.core.processes;

/** Tydzień 3, punkt 8 — dane identyfikujące proces. */
public record ProcessIdentity(long pid, String name, String user) {
}
