package pl.edu.devtools.core.urls;

import org.junit.jupiter.api.Test;
import pl.edu.devtools.core.UrlNode;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class UrlIteratorTest {
    @Test
    void traversesNestedGroupsPreservingOrderLeadingZerosAndDuplicates() {
        URI first = URI.create("http://localhost/003");
        URI second = URI.create("http://localhost/2");
        var root = new UrlGroup("root");
        var nested = new UrlGroup("nested");
        var deeper = new UrlGroup("deeper");
        root.add(new UrlGroup("empty"));
        root.add(nested);
        nested.add(new UrlLink(first));
        nested.add(deeper);
        deeper.add(new UrlGroup("empty"));
        deeper.add(new UrlLink(first));
        nested.add(new UrlLink(second));
        root.add(new UrlLink(second));

        var actual = new ArrayList<URI>();
        root.forEach(actual::add);
        assertEquals(List.of(first, first, second, second), actual);
    }

    @Test
    void iteratorsAreIndependentAndHasNextDoesNotAdvance() {
        var root = new UrlBuilder().template("localhost/{id}").parameters("1,2\n3").build();
        var left = root.iterator();
        var right = root.iterator();
        assertTrue(left.hasNext());
        assertTrue(left.hasNext());
        assertEquals(URI.create("http://localhost/1"), left.next());
        assertEquals(URI.create("http://localhost/2"), left.next());
        assertEquals(URI.create("http://localhost/1"), right.next());
        assertEquals(URI.create("http://localhost/3"), left.next());
        assertFalse(left.hasNext());
        assertEquals(URI.create("http://localhost/2"), right.next());
        assertEquals(URI.create("http://localhost/3"), right.next());
        assertFalse(right.hasNext());
    }

    @Test
    void exhaustedIteratorsRejectNextAndRemoval() {
        var link = new UrlLink(URI.create("http://localhost/1"));
        var group = new UrlGroup("single");
        group.add(link);
        for (UrlNode node : List.of(new UrlGroup("empty"), link, group)) {
            var iterator = node.iterator();
            while (iterator.hasNext()) {
                iterator.next();
            }
            assertFalse(iterator.hasNext());
            assertThrows(NoSuchElementException.class, iterator::next);
            assertThrows(UnsupportedOperationException.class, iterator::remove);
        }
    }

    @Test
    void visitsChildrenLazilyThroughTheirInterface() {
        var first = new CountingUrlNode(URI.create("http://localhost/1"));
        var second = new CountingUrlNode(URI.create("http://localhost/2"));
        var root = new UrlGroup("root");
        root.add(first);
        root.add(second);
        var iterator = root.iterator();
        assertEquals(0, first.iteratorCalls);
        assertEquals(0, second.iteratorCalls);
        assertTrue(iterator.hasNext());
        assertEquals(1, first.iteratorCalls);
        assertEquals(0, second.iteratorCalls);
        assertEquals(URI.create("http://localhost/1"), iterator.next());
        assertEquals(URI.create("http://localhost/2"), iterator.next());
        assertFalse(iterator.hasNext());
        assertEquals(1, second.iteratorCalls);
        assertEquals(0, first.urlsCalls);
        assertEquals(0, second.urlsCalls);
    }
}
