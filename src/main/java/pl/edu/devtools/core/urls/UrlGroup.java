package pl.edu.devtools.core.urls;

import pl.edu.devtools.core.UrlNode;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Tydzień 1, Wzorzec 5/9: Composite, zastosowanie 1.
 * Pojedynczy URL i zagnieżdżona grupa mają wspólny interfejs UrlNode.
 */
public final class UrlGroup implements UrlNode {
    private final String label;
    private final List<UrlNode> children = new ArrayList<>();

    public UrlGroup(String label) {
        this.label = label;
    }

    public void add(UrlNode child) {
        if (child == this || child instanceof UrlGroup group && group.contains(this)) {
            throw new IllegalArgumentException("Grupa nie może zawierać samej siebie.");
        }
        children.add(child);
    }

    private boolean contains(UrlNode node) {
        return children.stream().anyMatch(child -> child == node
                || child instanceof UrlGroup group && group.contains(node));
    }

    public void remove(UrlNode child) {
        children.remove(child);
    }

    public List<UrlNode> children() {
        return List.copyOf(children);
    }

    @Override
    public Iterator<URI> iterator() {
        return new UrlIterator(children().iterator());
    }

    @Override
    public List<URI> urls() {
        return children.stream().flatMap(child -> child.urls().stream()).toList();
    }

    /**
     * Tydzień 1, Wzorzec 3/9: Prototype, zastosowanie 1.
     * Kopia ma osobną listę i rekurencyjnie kopiuje dzieci, aby edycja podglądu
     * nie zmieniała prototypu używanego przez przycisk przywracania.
     */
    @Override
    public UrlGroup copy() {
        UrlGroup result = new UrlGroup(label);
        children.forEach(child -> result.add(child.copy()));
        return result;
    }
    /** Koniec, Tydzień 1, Wzorzec 3/9: Prototype, zastosowanie 1.*/

    @Override
    public String toString() {
        return label + " (" + urls().size() + ")";
    }
}
/** Koniec, Tydzień 1, Wzorzec 5/9: Composite, zastosowanie 1. */
