package pl.edu.devtools.core;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

public interface Processes {
    List<ProcessRow> list();

    boolean stop(long pid);

    // Tydzień 1, Wzorzec 4/9: Adapter, zastosowanie 1.
    // Tłumaczy API ProcessHandle na listę danych aplikacji i operację stop(pid).
    final class SystemProcesses implements Processes {
        @Override
        public List<ProcessRow> list() {
            try (var processes = ProcessHandle.allProcesses()) {
                return processes.filter(process -> process.pid() > 0 && process.isAlive()).map(process -> {
                    var info = process.info();
                    String command = info.command().orElse("");
                    String name = command.isEmpty() ? "(nazwa niedostępna)"
                            : Path.of(command).getFileName().toString();
                    return new ProcessRow(process.pid(), name, info.user().orElse("—"),
                            info.totalCpuDuration().map(value -> value.toMillis() / 1000.0).orElse(null),
                            command, info.startInstant().map(Object::toString).orElse("—"));
                }).sorted(Comparator.comparingLong(ProcessRow::pid)).toList();
            }
        }

        @Override
        public boolean stop(long pid) {
            return ProcessHandle.of(pid).filter(ProcessHandle::isAlive)
                    .map(ProcessHandle::destroy).orElse(false);
        }
    }
    // Koniec, Tydzień 1, Wzorzec 4/9: Adapter, zastosowanie 1.

    // Tydzień 1, Wzorzec 8/9: Proxy, zastosowanie 1.
    // Ten sam interfejs; żądanie zatrzymania jest przekazywane po kontroli PID.
    final class Guarded implements Processes {
        private final Processes delegate;
        private final long ownPid;

        public Guarded(Processes delegate, long ownPid) {
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
    // Koniec, Tydzień 1, Wzorzec 8/9: Proxy, zastosowanie 1.
}
