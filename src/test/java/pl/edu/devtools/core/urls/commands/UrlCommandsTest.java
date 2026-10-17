package pl.edu.devtools.core.urls.commands;

import org.junit.jupiter.api.Test;
import pl.edu.devtools.core.UrlNode;
import pl.edu.devtools.core.urls.UrlSession;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UrlCommandsTest {
    @Test
    void commandsCanBeQueuedBeforeExecution() {
        var session = session("1,2\n3");
        var original = session.batch();
        var parent = (UrlNode.Group) original.children().get(0);
        List<Command> commands = List.of(
                new RemoveUrlCommand(session, parent, parent.children().get(0)),
                new RestorePreviewCommand(session));

        assertEquals(List.of("/1", "/2", "/3"), paths(session));
        commands.get(0).execute();
        assertEquals(List.of("/2", "/3"), paths(session));
        commands.get(1).execute();
        assertEquals(List.of("/1", "/2", "/3"), paths(session));
        assertNotSame(original, session.batch());
    }

    @Test
    void removingGroupsDoesNotChangeThePrototype() {
        var session = session("1,2\n3,4");
        Command restore = new RestorePreviewCommand(session);
        new RemoveUrlCommand(session, session.batch(), session.batch().children().get(1)).execute();
        assertEquals(List.of("/1", "/2"), paths(session));
        restore.execute();
        new RemoveUrlCommand(session, session.batch(), session.batch().children().get(0)).execute();
        assertEquals(List.of("/3", "/4"), paths(session));
        restore.execute();
        assertEquals(List.of("/1", "/2", "/3", "/4"), paths(session));
    }

    @Test
    void restoreUsesCurrentPrototypeAndClearPreventsOldDataReturning() {
        var session = new UrlSession();
        Command restore = new RestorePreviewCommand(session);
        restore.execute();
        assertNull(session.batch());
        session.load(new UrlNode.Builder().template("localhost/{id}").parameters("1").build());
        session.load(new UrlNode.Builder().template("localhost/{id}").parameters("2,3").build());
        new RemoveUrlCommand(session, session.batch(), session.batch().children().get(0)).execute();
        assertTrue(paths(session).isEmpty());
        restore.execute();
        assertEquals(List.of("/2", "/3"), paths(session));
        session.clear();
        restore.execute();
        assertNull(session.batch());
    }

    private UrlSession session(String parameters) {
        var session = new UrlSession();
        session.load(new UrlNode.Builder().template("localhost/{id}").parameters(parameters).build());
        return session;
    }

    private List<String> paths(UrlSession session) {
        return session.batch().urls().stream().map(URI::getPath).toList();
    }
}
