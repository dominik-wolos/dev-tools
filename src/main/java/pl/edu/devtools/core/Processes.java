package pl.edu.devtools.core;

import java.util.List;

public interface Processes {
    List<ProcessRow> list();

    boolean stop(long pid);
}
