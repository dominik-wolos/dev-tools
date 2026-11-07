package pl.edu.devtools.core.urls;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UrlNodeFactorySubstitutionTest {
    @Test
    void baseAndDerivedObjectsCreateTheSameSingleLinkThroughTheBaseType() {
        URI address = URI.create("https://localhost/orders/003");
        for (UrlNodeFactory factory : factories()) {
            var link = assertInstanceOf(UrlLink.class, factory.create("jeden", List.of(address)));
            assertEquals(address, link.uri());
            assertEquals(List.of(address), link.urls());
        }
    }

    @Test
    void baseAndDerivedObjectsPreserveGroupLabelOrderAndDuplicates() {
        URI first = URI.create("https://localhost/orders/003");
        URI second = URI.create("mailto:owner@example.com");
        List<URI> addresses = List.of(first, first, second);
        for (UrlNodeFactory factory : factories()) {
            var group = assertInstanceOf(UrlGroup.class, factory.create("wiersz", addresses));
            assertEquals(addresses, group.urls());
            assertEquals("wiersz (3)", group.toString());
            assertEquals(3, group.children().size());
        }
    }

    @Test
    void baseAndDerivedObjectsRejectEmptyAddressLists() {
        for (UrlNodeFactory factory : factories()) {
            var failure = assertThrows(IllegalArgumentException.class,
                    () -> factory.create("pusty", List.of()));
            assertEquals("Lista adresów jest pusta.", failure.getMessage());
        }
    }

    private List<UrlNodeFactory> factories() {
        return List.of(new UrlNodeFactory(), new LoggingUrlNodeFactory());
    }
}
