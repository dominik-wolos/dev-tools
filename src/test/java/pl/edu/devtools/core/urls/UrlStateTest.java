package pl.edu.devtools.core.urls;

import org.junit.jupiter.api.Test;
import pl.edu.devtools.core.UrlNode;

import static org.junit.jupiter.api.Assertions.*;

class UrlStateTest {
    @Test
    void emptySessionCannotOpenAndLoadingEnablesOpening() {
        var session = new UrlSession();
        assertTrue(session.canEdit());
        assertThrows(IllegalStateException.class, session::beginOpening);
        session.finishOpening();
        assertThrows(IllegalStateException.class, session::beginOpening);

        session.load(original());
        var snapshot = session.beginOpening();
        assertEquals(session.batch().urls(), snapshot.urls());
        assertNotSame(session.batch(), snapshot);
        assertFalse(session.canEdit());
        session.finishOpening();
        assertTrue(session.canEdit());
        session.clear();
        assertThrows(IllegalStateException.class, session::beginOpening);
    }

    @Test
    void openingRejectsMutationsAndPreservesUndoHistory() {
        var session = new UrlSession();
        session.load(original());
        var batch = session.batch();
        session.remove(batch, batch.children().get(1));
        assertTrue(session.canUndo());
        var snapshot = session.beginOpening();

        assertThrows(IllegalStateException.class, session::beginOpening);
        assertThrows(IllegalStateException.class, session::clear);
        assertThrows(IllegalStateException.class, () -> session.load(original()));
        assertThrows(IllegalStateException.class, () -> session.remove(batch, batch.children().get(0)));
        assertThrows(IllegalStateException.class, session::restore);
        assertThrows(IllegalStateException.class, session::undo);
        assertFalse(session.canUndo());
        assertSame(batch, session.batch());
        assertEquals(1, snapshot.urls().size());

        session.finishOpening();
        assertTrue(session.canUndo());
        session.undo();
        assertEquals(2, session.batch().urls().size());
        assertEquals(1, snapshot.urls().size());
    }

    @Test
    void previewWithoutUrlsRemainsEditableAndCanBeRestored() {
        var session = new UrlSession();
        session.load(original());
        var batch = session.batch();
        for (var child : batch.children()) {
            session.remove(batch, child);
        }
        assertThrows(IllegalStateException.class, session::beginOpening);
        assertTrue(session.canEdit());
        assertTrue(session.canUndo());
        session.restore();
        assertEquals(2, session.beginOpening().urls().size());
    }

    @Test
    void completionPreservesPreviewAndAllowsRepeatedOpening() {
        var session = new UrlSession();
        session.load(original());
        var batch = session.batch();
        for (int attempt = 0; attempt < 2; attempt++) {
            assertEquals(2, session.beginOpening().urls().size());
            assertFalse(session.canEdit());
            session.finishOpening();
            assertTrue(session.canEdit());
            assertSame(batch, session.batch());
        }
    }

    private UrlNode.Group original() {
        return new UrlNode.Builder().template("localhost/{id}").parameters("1\n2").build();
    }
}
