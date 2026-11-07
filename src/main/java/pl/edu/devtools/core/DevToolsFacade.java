package pl.edu.devtools.core;

import pl.edu.devtools.core.json.TimedJsonTool;
import pl.edu.devtools.core.json.operations.Operation;
import pl.edu.devtools.core.report.ReportExporter;
import pl.edu.devtools.core.processes.ProcessListing;
import pl.edu.devtools.core.processes.ProcessStopping;
import pl.edu.devtools.core.urls.UrlCopyable;
import pl.edu.devtools.core.urls.UrlBuilder;
import pl.edu.devtools.core.urls.UrlGroup;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.stream.StreamSupport;

/**
 * Lab 1, zadanie 7: Facade, zastosowanie 1.
 * Widoki korzystają z jednej usługi łączącej JSON, procesy, budowanie URL-i i raporty.
 */
public final class DevToolsFacade implements ProcessListing, ProcessStopping {
    private final Processes processes;
    private final TimedJsonTool json;
    private final Browser browser;

    public DevToolsFacade(Processes processes, JsonTool json, Browser browser) {
        this.processes = processes;
        /**
         * Lab 1, zadanie 6: Decorator, zastosowanie 2.
        ** Każda operacja JSON-a w tej fasadzie przechodzi przez
        **dekorator TimedJsonTool, który dodaje pomiar czasu do przekazanego narzędzia.
        */
        this.json = new TimedJsonTool(json);
        /** Koniec, Lab 1, zadanie 6: Decorator, zastosowanie 2. */
        this.browser = browser;
    }

    @Override
    public List<ProcessRow> list() {
        return processes.list();
    }

    @Override
    public boolean stop(long pid) {
        return processes.stop(pid);
    }

    public String json(String input, Operation operation) throws IOException {
        return json.execute(input, operation);
    }

    public double jsonMillis() {
        return json.elapsedMillis();
    }

    public UrlGroup urls(String template, String parameters) {
        /** Lab 1, zadanie 2: Builder, zastosowanie 2.
        // Przycisk Podgląd przekazuje tu dane formularza: builder zbiera szablon
        // i parametry, a build() waliduje je i tworzy kompletny zestaw URL-i.
        */
        return new UrlBuilder().template(template).parameters(parameters).build();
        /** Koniec, Lab 1, zadanie 2: Builder, zastosowanie 2. */
    }

    /**
     * Lab 1, zadanie 4: Adapter, zastosowanie 4.
    **Otwieranie URL-i używa interfejsu Browser; w aplikacji DesktopBrowser
     **/
    public int open(UrlCopyable batch) {
        /** Lab 1, zadanie 5: Composite, zastosowanie 2.
        ** Przycisk Otwórz URL-e wymaga tylko kopiowania. Wynik ma wspólny typ UrlNode:
        ** UrlLink reprezentuje jeden adres, a UrlGroup zagnieżdżone grupy.
         */
        UrlNode addresses = batch.copy();
        /** Koniec, Lab 1, zadanie 5: Composite, zastosowanie 2. */
        return openAddresses(addresses);
    }

    private int openAddresses(Iterable<URI> addresses) {
        /**
         * Lab 2, zadanie 3: Iterator, zastosowanie 2.
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
        /** Koniec, Lab 2, zadanie 3: Iterator, zastosowanie 2. */
        return opened;
    }
    /** Koniec, Lab 1, zadanie 4: Adapter, zastosowanie 4. */

    public void export(List<String> lines, ReportExporter report) throws IOException {
        /**
         * Lab 2, zadanie 9: Template Method, zastosowanie 2.
         * Wspólne wywołanie metody szablonowej dla raportu TXT lub JSON.
         */
        report.export(lines);
        /** Koniec, Lab 2, zadanie 9: Template Method, zastosowanie 2. */
    }
}
/** Koniec, Lab 1, zadanie 7: Facade, zastosowanie 1. */
