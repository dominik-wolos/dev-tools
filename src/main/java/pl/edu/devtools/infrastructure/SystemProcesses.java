package pl.edu.devtools.infrastructure;

import pl.edu.devtools.core.ProcessRow;
import pl.edu.devtools.core.Processes;
import pl.edu.devtools.core.processes.ProcessIdentity;
import pl.edu.devtools.core.processes.ProcessDetails;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

/**
 * Lab 1, zadanie 4: Adapter, zastosowanie 1.
 * Tłumaczy API ProcessHandle na listę danych aplikacji i operację stop(pid).
 */
public final class SystemProcesses implements Processes {
    @Override
    public List<ProcessRow> list() {
        try (var processes = ProcessHandle.allProcesses()) {
            return processes.filter(process -> process.pid() > 0 && process.isAlive())
                    .map(this::row).sorted(Comparator.comparingLong(ProcessRow::pid)).toList();
        }
    }

    private ProcessRow row(ProcessHandle process) {
        var info = process.info();
        String command = info.command().orElse("");
        String name = command.isEmpty() ? "(nazwa niedostępna)" : Path.of(command).getFileName().toString();
        var identity = new ProcessIdentity(process.pid(), name, info.user().orElse("—"));
        var details = new ProcessDetails(
                info.totalCpuDuration().map(value -> value.toMillis() / 1000.0).orElse(null),
                command, info.startInstant().map(Object::toString).orElse("—"));
        return new ProcessRow(identity, details);
    }

    @Override
    public boolean stop(long pid) {
        return ProcessHandle.of(pid).filter(ProcessHandle::isAlive)
                .map(ProcessHandle::destroy).orElse(false);
    }
}
/** Koniec, Lab 1, zadanie 4: Adapter, zastosowanie 1. */
