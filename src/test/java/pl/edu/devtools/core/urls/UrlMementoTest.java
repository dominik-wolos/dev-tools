package pl.edu.devtools.core.urls;

import org.junit.jupiter.api.Test;
import pl.edu.devtools.core.UrlNode;
import pl.edu.devtools.core.urls.commands.Command;
import pl.edu.devtools.core.urls.commands.UndoPreviewCommand;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UrlMementoTest {
    @Test
    void undoRestoresOnlyTheLastRemovalAndConsumesHistory() {
        var session = session("1,2\n3");
        var parent = (UrlNode.Group) session.batch().children().get(0);
        session.remove(parent, parent.children().get(0));
        session.remove(session.batch(), session.batch().children().get(1));
        assertEquals(List.of("/2"), paths(session));
        assertTrue(session.canUndo());

        Command undo = new UndoPreviewCommand(session);
        undo.execute();
        assertEquals(List.of("/2", "/3"), paths(session));
        assertEquals(2, session.batch().children().size());
        assertInstanceOf(UrlNode.Group.class, session.batch().children().get(0));
        assertFalse(session.canUndo());
        undo.execute();
        assertEquals(List.of("/2", "/3"), paths(session));
    }

    @Test
    void undoOfRestoreReturnsEditedPreviewAndKeepsPrototypeIntact() {
        var session = session("1,2\n3");
        session.remove(session.batch(), session.batch().children().get(1));
        session.restore();
        assertEquals(List.of("/1", "/2", "/3"), paths(session));
        session.undo();
        assertEquals(List.of("/1", "/2"), paths(session));
        assertFalse(session.canUndo());
        session.restore();
        assertEquals(List.of("/1", "/2", "/3"), paths(session));
    }

    @Test
    void snapshotIsIndependentOfLaterWorkingTreeEdits() {
        var session = session("1,2\n3");
        var oldBatch = session.batch();
        var parent = (UrlNode.Group) oldBatch.children().get(0);
        session.remove(parent, parent.children().get(0));
        parent.remove(parent.children().get(0));
        oldBatch.add(new UrlNode.Link(URI.create("http://localhost/4")));

        session.undo();
        assertEquals(List.of("/1", "/2", "/3"), paths(session));
        parent.add(new UrlNode.Link(URI.create("http://localhost/5")));
        assertEquals(List.of("/1", "/2", "/3"), paths(session));
        assertNotSame(oldBatch, session.batch());
    }

    @Test
    void loadingAndClearingDiscardHistory() {
        var session = new UrlSession();
        session.undo();
        assertNull(session.batch());
        assertFalse(session.canUndo());
        session.load(new UrlNode.Builder().template("localhost/{id}").parameters("1,2").build());
        session.remove(session.batch(), session.batch().children().get(0));
        assertTrue(session.canUndo());

        session.load(new UrlNode.Builder().template("localhost/{id}").parameters("3,4").build());
        assertFalse(session.canUndo());
        session.undo();
        assertEquals(List.of("/3", "/4"), paths(session));
        session.remove(session.batch(), session.batch().children().get(0));
        assertTrue(session.canUndo());
        session.clear();
        session.undo();
        session.restore();
        assertNull(session.batch());
        assertFalse(session.canUndo());
    }

    @Test
    void ignoredRemovalsDoNotReplaceSavedState() {
        var original = new UrlNode.Builder().template("localhost/{id}").parameters("1,2\n3").build();
        var session = new UrlSession();
        session.load(original);
        var parent = (UrlNode.Group) session.batch().children().get(0);
        session.remove(parent, parent.children().get(0));
        session.remove(original, original.children().get(0));
        session.remove(parent, new UrlNode.Link(URI.create("http://localhost/999")));

        session.undo();
        assertEquals(List.of("/1", "/2", "/3"), paths(session));
        session.remove(session.batch(), new UrlNode.Link(URI.create("http://localhost/999")));
        assertFalse(session.canUndo());
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
