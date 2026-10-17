package pl.edu.devtools.core;

import pl.edu.devtools.core.json.operations.Operation;
import pl.edu.devtools.core.report.Report;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.stream.StreamSupport;

/**
 * Tydzień 1, Wzorzec 7/9: Facade, zastosowanie 1.
 * Widoki korzystają z jednej usługi łączącej JSON, procesy, budowanie URL-i i raporty.
 */
public final class DevToolsFacade {
    private final Processes processes;
    private final JsonTool.Timed json;
    private final Browser browser;

    public DevToolsFacade(Processes processes, JsonTool json, Browser browser) {
        this.processes = processes;
        /**
         * Tydzień 1, Wzorzec 6/9: Decorator, zastosowanie 2.
        ** Każda operacja JSON-a w tej fasadzie przechodzi przez
        **dekorator Timed, który dodaje pomiar czasu do przekazanego narzędzia.
        */
        this.json = new JsonTool.Timed(json);
        /** Koniec, Tydzień 1, Wzorzec 6/9: Decorator, zastosowanie 2. **/
        this.browser = browser;
    }

    public List<ProcessRow> processes() {
        return processes.list();
    }

    public boolean stop(long pid) {
        return processes.stop(pid);
    }

    public String json(String input, Operation operation) throws IOException {
        return json.execute(input, operation);
    }

    public double jsonMillis() {
        return json.elapsedMillis();
    }

    public UrlNode.Group urls(String template, String parameters) {
        /** Tydzień 1, Wzorzec 2/9: Builder, zastosowanie 2.
        // Przycisk Podgląd przekazuje tu dane formularza: builder zbiera szablon
        // i parametry, a build() waliduje je i tworzy kompletny zestaw URL-i.
        */
        return new UrlNode.Builder().template(template).parameters(parameters).build();
        /** Koniec, Tydzień 1, Wzorzec 2/9: Builder, zastosowanie 2. **/
    }

    /**
     * Tydzień 1, Wzorzec 4/9: Adapter, zastosowanie 4.
    **Otwieranie URL-i używa interfejsu Browser; w aplikacji DesktopBrowser
     **/
    public int open(UrlNode batch) {
        /** Tydzień 1, Wzorzec 5/9: Composite, zastosowanie 2.
        ** Przycisk Otwórz URL-e przyjmuje wspólny UrlNode: Link reprezentuje jeden
        ** adres, a Group może zawierać dowolnie zagnieżdżone grupy.
         */
        UrlNode addresses = batch.copy();
        /** Koniec, Tydzień 1, Wzorzec 5/9: Composite, zastosowanie 2.*/
        /**
         * Tydzień 2, Wzorzec 3: Iterator, zastosowanie 2.
         * Niezależne wywołanie iteratora zlicza adresy, a kolejne przekazuje je przeglądarce.
         */
        long total = StreamSupport.stream(addresses.spliterator(), false).count();
        int opened = 0;
        for (URI uri : addresses) {
            try {
                browser.open(uri);
                opened++;
            } catch (IOException | RuntimeException error) {
                throw new IllegalStateException("Otwarto " + opened + " z " + total
                        + " adresów. " + error.getMessage(), error);
            }
        }
        /** Koniec, Tydzień 2, Wzorzec 3: Iterator, zastosowanie 2. */
        return opened;
    }
    /** Koniec, Tydzień 1, Wzorzec 4/9: Adapter, zastosowanie 4. */

    public void export(List<String> lines, Report report) throws IOException {
        /**
         * Tydzień 2, Wzorzec 9: Template Method, zastosowanie 2.
         * Wspólne wywołanie metody szablonowej dla raportu TXT lub JSON.
         */
        report.export(lines);
        /** Koniec, Tydzień 2, Wzorzec 9: Template Method, zastosowanie 2. */
    }
}
/**Koniec, Tydzień 1, Wzorzec 7/9: Facade, zastosowanie 1.*/
