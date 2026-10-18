package pl.edu.devtools.core.processes;

import pl.edu.devtools.core.ProcessRow;
import pl.edu.devtools.core.Processes;

import java.util.List;

/**
 * Tydzień 1, Wzorzec 8/9: Proxy, zastosowanie 1.
 * Ten sam interfejs; żądanie zatrzymania jest przekazywane po kontroli PID.
 */
public final class GuardedProcesses implements Processes {
    private final Processes delegate;
    private final long ownPid;

    public GuardedProcesses(Processes delegate, long ownPid) {
        this.delegate = delegate;
        this.ownPid = ownPid;
    }

    @Override
    public List<ProcessRow> list() {
        return delegate.list();
    }

    @Override
    public boolean stop(long pid) {
        if (pid <= 1 || pid == ownPid) {
            throw new IllegalArgumentException("Nie można zatrzymać DevTools ani procesu systemowego PID 1.");
        }
        return delegate.stop(pid);
    }
}
/** Koniec, Tydzień 1, Wzorzec 8/9: Proxy, zastosowanie 1. */
