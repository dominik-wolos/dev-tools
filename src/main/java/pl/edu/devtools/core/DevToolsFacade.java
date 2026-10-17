package pl.edu.devtools.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.util.List;

/**
 * Tydzień 1, Wzorzec 7/9: Facade, zastosowanie 1.
 * Widoki korzystają z jednej usługi łączącej JSON, procesy, budowanie URL-i i raporty.
 */
public final class DevToolsFacade {
    private final Processes processes;
    private final JsonTool.Timed json;
    private final Browser browser;
    private final ObjectMapper reportMapper = new ObjectMapper();

    public DevToolsFacade(Processes processes, JsonTool json, Browser browser) {
        this.processes = processes;
        /**
         * Tydzień 1, Wzorzec 6/9: Decorator, zastosowanie 2.
        ** Każda walidacja i formatowanie JSON-a w tej fasadzie przechodzą przez
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

    public String json(String input, boolean format) throws IOException {
        return json.execute(input, format);
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
        ** Przycisk Otwórz URL-e pobiera adresy przez wspólne urls(): Link zwraca
        ** jeden adres, a Group zbiera je rekurencyjnie z dowolnie zagnieżdżonych grup.
         */
        List<URI> addresses = batch.copy().urls();
        /** Koniec, Tydzień 1, Wzorzec 5/9: Composite, zastosowanie 2.*/
        int opened = 0;
        for (URI uri : addresses) {
            try {

                browser.open(uri);
                opened++;
            } catch (IOException | RuntimeException error) {
                throw new IllegalStateException("Otwarto " + opened + " z " + addresses.size()
                        + " adresów. " + error.getMessage(), error);
            }
        }
        return opened;
    }
    /** Koniec, Tydzień 1, Wzorzec 4/9: Adapter, zastosowanie 4. */

    public void export(List<String> lines, boolean asJson, Report.Output destination) throws IOException {
        /** Tydzień 1, Wzorzec 9/9: Bridge, zastosowanie 4.
        ** Łączy wybrany format TXT/JSON z przekazanym miejscem zapisu: plikiem
        ** lub schowkiem. Format i miejsce zapisu można wybierać niezależnie.
         */
        Report report = asJson ? new Report.Json(destination, reportMapper) : new Report.Text(destination);
        report.export(lines);
        /** Koniec, Tydzień 1, Wzorzec 9/9: Bridge, zastosowanie 4.*/
    }
}
/**Koniec, Tydzień 1, Wzorzec 7/9: Facade, zastosowanie 1.*/
