package pl.edu.devtools.core;

import pl.edu.devtools.core.processes.ProcessIdentity;
import pl.edu.devtools.core.processes.ProcessDetails;

/** Tydzień 3, punkt 8 — dwa spójne obiekty danych zamiast sześciu argumentów konstruktora. */
public record ProcessRow(ProcessIdentity identity, ProcessDetails details) {
    public long pid() {
        return identity.pid();
    }

    public String name() {
        return identity.name();
    }

    public String user() {
        return identity.user();
    }

    public Double cpuSeconds() {
        return details.cpuSeconds();
    }

    public String command() {
        return details.command();
    }

    public String started() {
        return details.started();
    }
}
