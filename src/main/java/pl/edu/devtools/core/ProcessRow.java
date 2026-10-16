package pl.edu.devtools.core;

public record ProcessRow(long pid, String name, String user, Double cpuSeconds, String command, String started) {
}
