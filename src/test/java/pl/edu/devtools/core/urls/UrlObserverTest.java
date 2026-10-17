package pl.edu.devtools.core.urls;

import org.junit.jupiter.api.Test;
import pl.edu.devtools.core.UrlNode;
import pl.edu.devtools.core.urls.observers.UrlSessionObservable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UrlObserverTest {
    @Test
    void twoObserversReceiveCompletedDataAndStateChanges() {
        var session = new UrlSession();
        var first = new RecordingUrlObserver();
        var second = new RecordingUrlObserver();
        UrlSessionObservable observable = session;
        observable.addObserver(first);
        observable.addObserver(second);
        assertTrue(first.sizes.isEmpty());

        session.load(original());
        session.remove(session.batch(), session.batch().children().get(0));
        session.restore();
        session.undo();
        session.beginOpening();
        session.finishOpening();
        session.clear();

        assertEquals(List.of(2, 1, 2, 1, 1, 1, -1), first.sizes);
        assertEquals(List.of(true, true, true, true, false, true, true), first.editable);
        assertEquals(List.of(false, true, true, false, false, false, false), first.undo);
        assertEquals(first.sizes, second.sizes);
        assertEquals(first.editable, second.editable);
        assertEquals(first.undo, second.undo);
    }

    @Test
    void duplicateSubscriptionAndUnsubscriptionDoNotAffectOtherObservers() {
        var session = new UrlSession();
        var first = new RecordingUrlObserver();
        var second = new RecordingUrlObserver();
        session.addObserver(first);
        session.addObserver(first);
        session.addObserver(second);
        session.load(original());
        assertEquals(List.of(2), first.sizes);

        session.removeObserver(first);
        session.removeObserver(first);
        session.clear();
        assertEquals(List.of(2), first.sizes);
        assertEquals(List.of(2, -1), second.sizes);
        assertThrows(NullPointerException.class, () -> session.addObserver(null));
    }

    @Test
    void rejectedOrIgnoredActionsDoNotNotifyObservers() {
        var session = new UrlSession();
        var observer = new RecordingUrlObserver();
        session.addObserver(observer);
        session.restore();
        session.undo();
        session.finishOpening();
        assertThrows(IllegalStateException.class, session::beginOpening);
        assertTrue(observer.sizes.isEmpty());

        session.load(new UrlNode.Group("Empty"));
        assertThrows(IllegalStateException.class, session::beginOpening);
        assertEquals(List.of(0), observer.sizes);

        session.load(original());
        var foreign = original();
        session.remove(foreign, foreign.children().get(0));
        session.undo();
        session.finishOpening();
        assertThrows(NullPointerException.class, () -> session.load(null));
        assertEquals(List.of(0, 2), observer.sizes);

        session.beginOpening();
        assertThrows(IllegalStateException.class, session::beginOpening);
        assertThrows(IllegalStateException.class, session::clear);
        assertThrows(IllegalStateException.class, session::restore);
        assertThrows(IllegalStateException.class, session::undo);
        assertEquals(List.of(0, 2, 2), observer.sizes);
    }

    private UrlNode.Group original() {
        return new UrlNode.Builder().template("localhost/{id}").parameters("1\n2").build();
    }
}
