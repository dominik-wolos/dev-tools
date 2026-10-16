# Tydzień 1 — Wzorce konstrukcyjne i strukturalne

Projekt DevTools na [zajęcia laboratoryjne](https://moodle3.cs.pollub.pl/course/view.php?id=1712).
Zasady opracowano na podstawie treści przekazanej przez prowadzącego i użytkownika.

## Zakres i kolejność realizacji

1. Uproszczenie okna do trzech zakładek: Processes, JSON i URLs.
2. Walidacja i formatowanie JSON-a przez Jacksona; Decorator mierzy czas operacji.
3. Lista i szczegóły procesów przez Adapter; Proxy kontroluje zatrzymywanie.
4. Generowanie i podgląd URL-i: Builder, Simple Factory, Composite i Prototype.
5. Wspólna Facade dla widoków oraz Bridge do eksportu wyników.
6. Wydzielenie implementacji przeglądarki, pliku i schowka do osobnych plików,
   testy działania oraz oznaczenia wymagane w kodzie.

Te etapy są zaimplementowane. Pierwotny stan obejmował wyłącznie makietę UI
z przyciskami bez obsługi zdarzeń; obecnie każda z trzech zakładek wykonuje operacje.

## Dziewięć wzorców

Numery 1–9 identyfikują różne wzorce i są takie same w README oraz komentarzach kodu.
Numer zastosowania oznacza kolejne użycie tego samego wzorca. Przykład:
`Wzorzec 4/9: Adapter, zastosowanie 2` to drugi adapter, a nadal czwarty wzorzec.

| Nr | Wzorzec | Zastosowanie w aplikacji | Co demonstrujemy |
| --- | --- | --- | --- |
| 1 | Simple Factory | `UrlNode.create()` wybiera `Link` dla jednego URL-a albo `Group` dla kilku. | Wspólny typ produktu i dwa konkretne produkty wybierane na podstawie wejścia. |
| 2 | Builder | `UrlNode.Builder` zbiera szablon i listę parametrów; `build()` waliduje dane i tworzy drzewo. | Gotowy, poprawny zestaw z wieloma adresami i grupami zamiast tworzenia niekompletnych obiektów. |
| 3 | Prototype | `Group.copy()` rekurencyjnie kopiuje dzieci; podgląd można przywrócić z oryginału. | Usunięcie adresu z roboczej kopii nie zmienia prototypu. |
| 4 | Adapter | `Processes.SystemProcesses` adaptuje `ProcessHandle`, a `DesktopBrowser` adaptuje `Desktop.browse()`. | Interfejsy aplikacji różnią się od API systemowych. |
| 5 | Composite | `Link` i `Group` implementują `UrlNode`; grupa zawiera inne węzły, w tym grupy. | Cały zestaw, wiersz parametrów i pojedynczy adres można jednakowo przekazać do `urls()`. |
| 6 | Decorator | `JsonTool.Timed` opakowuje dowolny `JsonTool` i mierzy czas w `finally`. | Ten sam interfejs i wynik; dodatkowa funkcja bez zmian w Jacksonie. |
| 7 | Facade | `DevToolsFacade` udostępnia operacje trzech narzędzi i eksportu. | Widoki nie muszą składać kolejnych kroków pracy podsystemów. |
| 8 | Proxy | `Processes.Guarded` ma interfejs `Processes` i deleguje po sprawdzeniu PID. | Blokuje własny proces i PID 1; inne żądania trafiają do usługi systemowej. |
| 9 | Bridge | `Report.Text` i `Report.Json` używają `Report.Output`; implementacje to `FileReportOutput` i `ClipboardReportOutput`. | TXT/JSON × plik/schowek: cztery kombinacje bez czterech klas eksportera. |

Wykorzystano wszystkie dziewięć pozycji pozostających na liście po pominięciu
Singletona i Flyweight. To dziewięć różnych wzorców, niezależnie od liczby zastosowań
Adaptera lub Bridge. Ocenę i przyznanie punktów ustala prowadzący.

## Uproszczenie i rozdzielenie odpowiedzialności

Kod produkcyjny ma 16 plików i 26 nazwanych typów: **19 klas + 2 rekordy + 5 interfejsów**.
To liczba uwzględniająca typy zagnieżdżone; przeniesienie klasy do wnętrza innej nie
zmniejsza tej liczby. Nie są tu wliczone testy ani klasy bibliotek.

Wspólne role pozostają razem w plikach `UrlNode`, `JsonTool`, `Processes` i `Report`.
Oddzielne pliki integracji mają po jednej odpowiedzialności. Implementacje zapisu
pliku, schowka i przeglądarki są nazwanymi klasami, a nie lambdami osadzonymi w UI.
Obsługa eksportu jest w `ui/ReportExportView.java`, w metodach `copy()` i `save()`;
przyciski używają referencji do metod. Krótkie lambdy w zakładkach obsługują zdarzenia,
filtrowanie i pracę asynchroniczną.

- **SRP:** formatowanie raportu, zapis do pliku, zapis do schowka i otwieranie
  przeglądarki mają oddzielne implementacje; widoki obsługują kontrolki.
- **OCP:** nowy cel eksportu implementuje `Report.Output`; oba formaty raportu mogą
  go użyć bez zmiany swoich klas. Analogicznie można podmienić `Browser`.
- **LSP:** proxy i dekorator zachowują interfejs opakowywanego obiektu.
- **ISP:** `Browser` i `Report.Output` mają po jednej metodzie. Interfejs `Processes`
  obejmuje dwie używane operacje; jego ewentualny dalszy podział nie jest potrzebny
  do obecnych trzech zakładek.
- **DIP:** fasada otrzymuje interfejsy procesów, JSON-a i przeglądarki przez
  konstruktor. Klasa aplikacji składa zależności. `core` nie importuje klas UI,
  JavaFX ani implementacji z `infrastructure`.

Nie dodawano bazy danych, repozytoriów ani konfigurowalnych profili. Stan podglądu
pozostaje w pamięci, a raporty zapisuje się na żądanie.

## Pliki, metody i zakresy linii

Ścieżki są względem `src/main/java/pl/edu/devtools/`. Zakresy obejmują komentarze
początku i końca wymagane na zajęciach. Numer tygodnia: 1.

Oznaczenia występują zarówno przy implementacjach, jak i przy rzeczywistych
wywołaniach w aplikacji. Komentarz przed wywołaniem opisuje przycisk lub operację,
która z niego korzysta. Dalsze zastosowania tego samego wzorca zachowują jego stały
numer 1–9.

| Nr | Wzorzec | Zastosowanie | Plik | Linie | Metody lub elementy |
| --- | --- | --- | --- | --- | --- |
| 1 | Simple Factory | 1 | `core/UrlNode.java` | 15–28 | `UrlNode.create()` |
| 1 | Simple Factory | 2 | `core/UrlNode.java` | 148–152 | `Builder.build()` — wywołanie fabryki dla wiersza parametrów |
| 2 | Builder | 1 | `core/UrlNode.java` | 98–161 | `Builder.template()`, `parameters()`, `build()` |
| 2 | Builder | 2 | `core/DevToolsFacade.java` | 47–51 | `urls()` — budowanie zestawu z danych formularza |
| 3 | Prototype | 1 | `core/UrlNode.java` | 80–89 | `Group.copy()` |
| 3 | Prototype | 2 | `ui/tabs/UrlsTabView.java` | 41–44 | obsługa przycisku Przywróć podgląd w `UrlsTabView.create()` |
| 3 | Prototype | 3 | `ui/tabs/UrlsTabView.java` | 69–73 | `generate()` — zapis niezależnego prototypu podglądu |
| 4 | Adapter | 1 | `core/Processes.java` | 12–36 | `SystemProcesses.list()`, `stop()` |
| 4 | Adapter | 2 | `infrastructure/DesktopBrowser.java` | 9–20 | `DesktopBrowser.open()` |
| 4 | Adapter | 3 | `core/DevToolsFacade.java` | 27–31 | `processes()` — pobranie danych przez adapter procesów |
| 4 | Adapter | 4 | `core/DevToolsFacade.java` | 63–67 | `open()` — otwieranie adresu przez adapter przeglądarki |
| 5 | Composite | 1 | `core/UrlNode.java` | 8–96 | `UrlNode.urls()`, `Link.urls()`, `Group.add()`, `remove()`, `urls()` |
| 5 | Composite | 2 | `core/DevToolsFacade.java` | 55–59 | `open()` — zebranie adresów z drzewa przez wspólne `urls()` |
| 6 | Decorator | 1 | `core/JsonTool.java` | 32–56 | `Timed.execute()`, `elapsedMillis()` |
| 6 | Decorator | 2 | `core/DevToolsFacade.java` | 18–22 | konstruktor — opakowanie narzędzia JSON dekoratorem |
| 7 | Facade | 1 | `core/DevToolsFacade.java` | 8–86 | `processes()`, `stop()`, `json()`, `urls()`, `open()`, `export()` |
| 7 | Facade | 2 | `ui/tabs/JsonTabView.java` | 51–55 | `run()` — Waliduj/Formatuj korzysta z fasady |
| 7 | Facade | 3 | `ui/tabs/UrlsTabView.java` | 64–68 | `generate()` — Podgląd korzysta z fasady |
| 7 | Facade | 4 | `ui/tabs/ProcessesTabView.java` | 81–93 | `refresh()` — Odśwież korzysta z fasady w tle |
| 8 | Proxy | 1 | `core/Processes.java` | 38–62 | `Guarded.list()`, `stop()` |
| 8 | Proxy | 2 | `DevToolsApplication.java` | 24–28 | inicjalizacja `tools` — przekazanie proxy do fasady |
| 9 | Bridge | 1 | `core/Report.java` | 8–54 | `Report.export()`, `Text.render()`, `Json.render()`, `Output.write()` |
| 9 | Bridge | 2 | `infrastructure/FileReportOutput.java` | 10–24 | `FileReportOutput.write()` |
| 9 | Bridge | 3 | `infrastructure/ClipboardReportOutput.java` | 9–21 | `ClipboardReportOutput.write()` |
| 9 | Bridge | 4 | `core/DevToolsFacade.java` | 78–83 | `export()` — połączenie formatu z miejscem zapisu |
| 9 | Bridge | 5 | `ui/ReportExportView.java` | 42–46 | `copy()` — połączenie TXT/JSON ze schowkiem |
| 9 | Bridge | 6 | `ui/ReportExportView.java` | 63–67 | `save()` — połączenie TXT/JSON z plikiem |

## Sprawdzenie działania

`mvn test`: **20 testów, 0 błędów, 0 niepowodzeń**. Zakres:

- poprawny/błędny/pusty JSON, dodatkowy dokument po JSON-ie, zachowanie precyzji liczb;
- wynik i propagacja błędów dekoratora;
- mieszane separatory, nowe linie, powtórzenia, zera wiodące i duże identyfikatory;
- nazwa placeholdera, port, query string, powtarzający się placeholder i błędne URL-e;
- dwa produkty fabryki, zagnieżdżone grupy, blokada cykli i głębokie kopiowanie;
- delegowanie przez proxy i odczyt bieżącego JVM z prawdziwej listy procesów;
- oba formaty raportów i dwie podstawione destynacje, w tym rzeczywisty zapis UTF-8
  przez `FileReportOutput`; propagacja błędu zapisu;
- otwieranie wybranych URL-i przez podstawioną przeglądarkę i częściowa awaria.

Testy nie zatrzymują prawdziwych procesów ani nie otwierają kart przeglądarki.
Schowek w testach jest zastąpiony pamięcią; jego implementacja JavaFX działa z UI.
System może ukrywać nazwę, ścieżkę albo czas CPU niektórych procesów — aplikacja
wyświetla brakujące dane, zamiast dopisywać fikcyjne wartości o obciążeniu CPU.
Wartość CPU w tabeli to łączny czas procesora w sekundach, nie procent obciążenia.

## Scenariusz prezentacji na zajęciach

1. Processes: pokaż listę, filtr PID/nazwy, sortowanie oraz szczegóły wybranego
   procesu. W teście proxy pokaż zablokowanie PID DevTools bez zatrzymywania programów.
2. JSON: wklej poprawny JSON, wybierz Waliduj i Formatuj; następnie pokaż błąd składni
   z wierszem/kolumną. Omów czas dodawany przez dekorator.
3. URLs: ustaw `localhost/orders/{orderID}` oraz listę `1,2` w pierwszym wierszu,
   `003;4` w drugim. Pokaż grupy i cztery adresy z prefiksem `http://`.
4. Usuń pojedynczy URL lub grupę, a potem wybierz Przywróć podgląd. Omów niezależną
   kopię prototypu. Zmień dane wejściowe i pokaż unieważnienie starego podglądu.
5. Otwórz adresy własnego środowiska testowego; aplikacja przekazuje je domyślnej
   przeglądarce i nie sprawdza dostępności serwera HTTP.
6. Wyeksportuj wynik w czterech kombinacjach TXT/JSON i plik/schowek; wskaż oddzielne
   hierarchie Bridge i implementacje zapisane w osobnych plikach.

Materiały do opisu wzorców: [katalog Refactoring.Guru](https://refactoring.guru/pl/design-patterns),
[porównanie fabryk](https://refactoring.guru/design-patterns/factory-comparison),
[Composite](https://refactoring.guru/pl/design-patterns/composite),
[Prototype](https://refactoring.guru/pl/design-patterns/prototype),
[Bridge](https://refactoring.guru/pl/design-patterns/bridge).
Kod aplikacji jest własną implementacją dopasowaną do tych narzędzi.
