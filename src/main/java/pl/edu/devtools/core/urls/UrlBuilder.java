package pl.edu.devtools.core.urls;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Lab 1, zadanie 2: Builder, zastosowanie 1.
 * Składa zestaw z szablonu i parametrów; build() sprawdza placeholder i adresy.
 */
public final class UrlBuilder {
    private static final Pattern PLACEHOLDER = Pattern.compile("\\{([A-Za-z][A-Za-z0-9_]*)}");
    /** Lab 3, zadanie 1: Liskov, zastosowanie 2 — builder używa typu bazowego fabryki. */
    private final UrlNodeFactory factory = new LoggingUrlNodeFactory();
    /** Koniec, Lab 3, zadanie 1: Liskov, zastosowanie 2. */
    private String template = "";
    private String parameters = "";

    public UrlBuilder template(String template) {
        this.template = template == null ? "" : template.trim();
        return this;
    }

    public UrlBuilder parameters(String parameters) {
        this.parameters = parameters == null ? "" : parameters;
        return this;
    }

    /** Lab 3, zadanie 5: Metody do 20 linii, zastosowanie 1 — przebieg podzielony na krótkie kroki. */
    /** Lab 3, zadanie 7: Jeden poziom abstrakcji, zastosowanie 1 — szczegóły budowania znajdują się poniżej. */
    public UrlGroup build() {
        String placeholder = placeholder();
        String normalized = template.contains("://") ? template : "http://" + template;
        UrlGroup result = groups(normalized, placeholder);
        if (result.children().isEmpty()) {
            throw new IllegalArgumentException("Podaj co najmniej jeden identyfikator liczbowy.");
        }
        return result;
    }

    /** Lab 3, zadanie 6: Jedna rola funkcji, zastosowanie 1 — walidacja i wyznaczenie parametru szablonu. */
    private String placeholder() {
        var matcher = PLACEHOLDER.matcher(template);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Podaj szablon z parametrem, np. localhost/orders/{orderID}.");
        }
        String placeholder = matcher.group();
        String withoutPlaceholder = template.replace(placeholder, "0");
        if (withoutPlaceholder.contains("{") || withoutPlaceholder.contains("}")) {
            throw new IllegalArgumentException("Użyj jednej nazwy parametru. Może powtarzać się w adresie.");
        }
        return placeholder;
    }

    private UrlGroup groups(String normalized, String placeholder) {
        UrlGroup result = new UrlGroup("Zestaw URL");
        int lineNumber = 0;
        for (String line : parameters.split("\\R")) {
            lineNumber++;
            List<URI> addresses = addresses(line, normalized, placeholder);
            if (!addresses.isEmpty()) {
                addGroup(result, addresses, lineNumber);
            }
        }
        return result;
    }

    private List<URI> addresses(String line, String normalized, String placeholder) {
        List<URI> addresses = new ArrayList<>();
        for (String value : line.split("[^0-9]+")) {
            if (!value.isEmpty()) {
                addresses.add(address(normalized, placeholder, value));
            }
        }
        return addresses;
    }

    private URI address(String normalized, String placeholder, String value) {
        URI uri;
        try {
            uri = URI.create(normalized.replace(placeholder, value));
        } catch (IllegalArgumentException error) {
            throw new IllegalArgumentException("Nieprawidłowy szablon URL: " + error.getMessage());
        }
        validateAddress(uri);
        return uri;
    }

    private void validateAddress(URI uri) {
        if (!("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()))
                || uri.getHost() == null || uri.getRawUserInfo() != null || uri.getPort() > 65535) {
            throw new IllegalArgumentException("Adres musi używać HTTP/HTTPS i mieć poprawną nazwę hosta.");
        }
    }

    private void addGroup(UrlGroup result, List<URI> addresses, int lineNumber) {
        /** Lab 1, zadanie 1: Simple Factory, zastosowanie 2.
        Podgląd URL-i: fabryka wybiera UrlLink dla jednego adresu w wierszu
         albo UrlGroup dla kilku; builder dodaje gotowy węzeł do zestawu.
         */
        result.add(factory.create("Wiersz " + lineNumber, addresses));
        /** Koniec, Lab 1, zadanie 1: Simple Factory, zastosowanie 2. */
    }
}
/** Koniec, Lab 1, zadanie 2: Builder, zastosowanie 1. */
