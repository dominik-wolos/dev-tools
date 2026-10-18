package pl.edu.devtools.core;

import java.util.List;

final class RecordingProcesses implements Processes {
    private long stoppedPid = -1;

    @Override
    public List<ProcessRow> list() {
        return List.of();
    }

    @Override
    public boolean stop(long pid) {
        stoppedPid = pid;
        return true;
    }

    long stoppedPid() {
        return stoppedPid;
    }
}
