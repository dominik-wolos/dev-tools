package pl.edu.devtools.core;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

// Tydzień 1, Wzorzec 5/9: Composite, zastosowanie 1.
// Pojedynczy URL i zagnieżdżona grupa mają wspólny interfejs urls().
public interface UrlNode {
    List<URI> urls();

    UrlNode copy();

    // Tydzień 1, Wzorzec 1/9: Simple Factory, zastosowanie 1.
    // Na podstawie liczby adresów wybiera konkretny produkt: Link albo Group.
    static UrlNode create(String label, List<URI> addresses) {
        if (addresses.isEmpty()) {
            throw new IllegalArgumentException("Lista adresów jest pusta.");
        }
        if (addresses.size() == 1) {
            return new Link(addresses.get(0));
        }
        Group group = new Group(label);
        addresses.forEach(uri -> group.add(new Link(uri)));
        return group;
    }
    // Koniec, Tydzień 1, Wzorzec 1/9: Simple Factory, zastosowanie 1.

    record Link(URI uri) implements UrlNode {
        @Override
        public List<URI> urls() {
            return List.of(uri);
        }

        @Override
        public Link copy() {
            return new Link(uri);
        }

        @Override
        public String toString() {
            return uri.toString();
        }
    }

    final class Group implements UrlNode {
        private final String label;
        private final List<UrlNode> children = new ArrayList<>();

        public Group(String label) {
            this.label = label;
        }

        public void add(UrlNode child) {
            if (child == this || child instanceof Group group && group.contains(this)) {
                throw new IllegalArgumentException("Grupa nie może zawierać samej siebie.");
            }
            children.add(child);
        }

        private boolean contains(UrlNode node) {
            return children.stream().anyMatch(child -> child == node
                    || child instanceof Group group && group.contains(node));
        }

        public void remove(UrlNode child) {
            children.remove(child);
        }

        public List<UrlNode> children() {
            return List.copyOf(children);
        }

        @Override
        public List<URI> urls() {
            return children.stream().flatMap(child -> child.urls().stream()).toList();
        }

        // Tydzień 1, Wzorzec 3/9: Prototype, zastosowanie 1.
        // Kopia ma osobną listę i rekurencyjnie kopiuje dzieci, aby edycja podglądu
        // nie zmieniała prototypu używanego przez przycisk przywracania.
        @Override
        public Group copy() {
            Group result = new Group(label);
            children.forEach(child -> result.add(child.copy()));
            return result;
        }
        // Koniec, Tydzień 1, Wzorzec 3/9: Prototype, zastosowanie 1.

        @Override
        public String toString() {
            return label + " (" + urls().size() + ")";
        }
    }
    // Koniec, Tydzień 1, Wzorzec 5/9: Composite, zastosowanie 1.

    // Tydzień 1, Wzorzec 2/9: Builder, zastosowanie 1.
    // Składa zestaw z szablonu i parametrów; build() sprawdza placeholder i adresy.
    final class Builder {
        private static final Pattern PLACEHOLDER = Pattern.compile("\\{([A-Za-z][A-Za-z0-9_]*)}");
        private String template = "";
        private String parameters = "";

        public Builder template(String template) {
            this.template = template == null ? "" : template.trim();
            return this;
        }

        public Builder parameters(String parameters) {
            this.parameters = parameters == null ? "" : parameters;
            return this;
        }

        public Group build() {
            var matcher = PLACEHOLDER.matcher(template);
            if (!matcher.find()) {
                throw new IllegalArgumentException("Podaj szablon z parametrem, np. localhost/orders/{orderID}.");
            }
            String placeholder = matcher.group();
            String withoutPlaceholder = template.replace(placeholder, "0");
            if (withoutPlaceholder.contains("{") || withoutPlaceholder.contains("}")) {
                throw new IllegalArgumentException("Użyj jednej nazwy parametru. Może powtarzać się w adresie.");
            }
            String normalized = template.contains("://") ? template : "http://" + template;
            Group result = new Group("Zestaw URL");
            int lineNumber = 0;
            for (String line : parameters.split("\\R")) {
                lineNumber++;
                List<URI> addresses = new ArrayList<>();
                for (String value : line.split("[^0-9]+")) {
                    if (!value.isEmpty()) {
                        URI uri;
                        try {
                            uri = URI.create(normalized.replace(placeholder, value));
                        } catch (IllegalArgumentException error) {
                            throw new IllegalArgumentException("Nieprawidłowy szablon URL: " + error.getMessage());
                        }
                        if (!("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()))
                                || uri.getHost() == null || uri.getRawUserInfo() != null
                                || uri.getPort() > 65535) {
                            throw new IllegalArgumentException("Adres musi używać HTTP/HTTPS i mieć poprawną nazwę hosta.");
                        }
                        addresses.add(uri);
                    }
                }
                if (!addresses.isEmpty()) {
                    // Tydzień 1, Wzorzec 1/9: Simple Factory, zastosowanie 2.
                    // Podgląd URL-i: fabryka wybiera Link dla jednego adresu w wierszu
                    // albo Group dla kilku; builder dodaje gotowy węzeł do zestawu.
                    result.add(UrlNode.create("Wiersz " + lineNumber, addresses));
                    // Koniec, Tydzień 1, Wzorzec 1/9: Simple Factory, zastosowanie 2.
                }
            }
            if (result.children().isEmpty()) {
                throw new IllegalArgumentException("Podaj co najmniej jeden identyfikator liczbowy.");
            }
            return result;
        }
    }
    // Koniec, Tydzień 1, Wzorzec 2/9: Builder, zastosowanie 1.
}
