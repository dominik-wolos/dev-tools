# DevTools

JavaFX application for developer workstation utilities.

## Requirements

- JDK 17
- Maven 3.9+

## Run

```bash
mvn javafx:run
```

In IntelliJ IDEA, import the project as a Maven project and run the `pl.edu.devtools.Launcher`
class. Do not run `DevToolsApplication` directly.

## Structure

- `pl.edu.devtools.DevToolsApplication` starts JavaFX and owns the stage lifecycle.
- `pl.edu.devtools.ui` contains the application shell and shared view composition.
- `pl.edu.devtools.ui.tabs` contains one class per workspace tab.
- `pl.edu.devtools.ui.model` contains simple UI data records.

## Test

```bash
mvn test
```
