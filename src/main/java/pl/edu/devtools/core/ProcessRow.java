package pl.edu.devtools.core;

import pl.edu.devtools.core.processes.ProcessIdentity;
import pl.edu.devtools.core.processes.ProcessDetails;

/** Lab 3, zadanie 8: Maksymalnie 3 argumenty, zastosowanie 1 — dwa spójne obiekty danych zamiast sześciu argumentów konstruktora. */
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
