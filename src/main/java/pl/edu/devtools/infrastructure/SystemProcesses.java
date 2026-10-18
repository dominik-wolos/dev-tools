package pl.edu.devtools.infrastructure;

import pl.edu.devtools.core.ProcessRow;
import pl.edu.devtools.core.Processes;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

/**
 * Tydzień 1, Wzorzec 4/9: Adapter, zastosowanie 1.
 * Tłumaczy API ProcessHandle na listę danych aplikacji i operację stop(pid).
 */
public final class SystemProcesses implements Processes {
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
/** Koniec, Tydzień 1, Wzorzec 4/9: Adapter, zastosowanie 1. */
