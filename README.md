# DevTools

Aplikacja Java 17 + JavaFX na zajęcia
[Wzorce projektowe i techniki pisania czystego kodu](https://moodle3.cs.pollub.pl/course/view.php?id=1712).

## Funkcje

- **Processes** — lista uruchomionych procesów, sortowanie, filtr nazwy/PID/użytkownika,
  odświeżanie, ścieżka programu, czas uruchomienia i łączny czas CPU.
  Zatrzymanie wymaga potwierdzenia; proxy blokuje własny PID oraz PID 1.
  Brak uprawnień systemowych do szczegółów procesu jest oznaczany w tabeli.
- **JSON** — walidacja, formatowanie i minifikacja przez Jacksona, opis błędu z wierszem/kolumną,
  pomiar czasu. Walidacja odrzuca pusty tekst i dodatkowe dane po dokumencie.
- **URLs** — podgląd i otwieranie adresów z szablonu z jednym nazwanym parametrem,
  np. `localhost/orders/{orderID}`. Każdy znak poza cyframi `0–9` jest separatorem.
  Lista `1, 2;003 | 4` tworzy cztery adresy. Kolejność, powtórzenia i zera wiodące
  są zachowywane; brak protokołu oznacza `http://`.
  Można usunąć URL lub całą grupę z podglądu, a następnie przywrócić pierwotny zestaw.
  „Cofnij” odtwarza stan sprzed ostatniego usunięcia lub przywrócenia podglądu.
- Wyniki każdej zakładki można wyeksportować jako TXT lub JSON do pliku albo schowka.
  Eksport JSON jest tablicą tekstowych wyników (nie modelem danych procesów).

Nowa linia na liście parametrów tworzy osobną grupę URL-i. Inne znaki spoza `0–9`
rozdzielają identyfikatory w tej samej grupie. `1-3` oznacza dwa identyfikatory: `1` i `3`,
a nie zakres; identyfikatory są nieujemnymi ciągami cyfr.

## Uruchomienie

W katalogu `DevTools`, z JDK 17 i Maven 3.9+:

```bash
mvn javafx:run
```

Wzorce z laboratorium 1 mają stałe numery 1–9, używane także w komentarzach kodu:

1. **Simple Factory**
2. **Builder**
3. **Prototype**
4. **Adapter**
5. **Composite**
6. **Decorator**
7. **Facade**
8. **Proxy**
9. **Bridge**

## Laboratorium 2

Pierwszy etap: **8. Strategy** — klasy `Validate`, `Format` i `Minify` implementują
interfejs `Operation` w pakiecie `core.json.operations`; każdy typ ma własny plik.
Ich obiekty są dostępne jako `VALIDATE`, `FORMAT` i `MINIFY`.
`JacksonJsonTool.execute()` w pakiecie `core.json` parsuje dane raz
i wykonuje przekazaną strategię. Widok `JsonTabView` wybiera ją przyciskiem,
a fasada i dekorator czasu przekazują ją dalej. Nową operację dodaje się jako
klasę implementującą `Operation`.

Drugi etap: **9. Template Method** — metoda `Report.export()` oznaczona `final` ustala
przebieg: walidacja danych → `render()` → zapis. `TextReport` i `JsonReport`
dostarczają krok renderowania. Puste dane są odrzucane przed renderowaniem
i zapisem; wywołanie szablonu znajduje się w `DevToolsFacade.export()`.

Pakiet `core.report` zawiera osobne pliki: `Report`, `ReportOutput`, `TextReport`
i `JsonReport`. Szablon zarządza przebiegiem, podklasy odpowiadają za format,
a implementacje `ReportOutput` za zapis. Fasada przyjmuje gotowy `Report`;
wybór formatu i miejsca zapisu pozostaje przy formularzu eksportu.

Trzeci etap: **3. Iterator** — `UrlIterator` w pakiecie `core.urls` wykonuje
leniwe przejście DFS po iteratorach dzieci. `UrlNode` implementuje `Iterable<URI>`;
otwieranie i eksport korzystają z tej samej kolejności, zachowując duplikaty.
Każde wywołanie `iterator()` zwraca niezależny iterator tylko do odczytu.
Podczas iteracji nie należy zmieniać drzewa; otwieranie używa jego kopii.

Czwarty etap: **1. Command** — `RemoveUrlCommand` i `RestorePreviewCommand`
w pakiecie `core.urls.commands` implementują `Command.execute()`. Polecenia
przechowują parametry operacji, a `UrlSession` zarządza podglądem i prototypem.
Sesja zapisuje niezależny prototyp i edytuje jego kopię roboczą; przywrócenie
ponownie klonuje prototyp. Drzewo przekazane do sesji pozostaje niezależne.
Przyciski zakładki URLs wywołują komendy przez wspólny interfejs; model i komendy
można uruchamiać oraz testować bez JavaFX. Każdy typ ma własny plik.

Piąty etap: **5. Memento** — `UrlMemento` ukrywa głęboką kopię bieżącego podglądu,
a `UrlHistory` przechowuje jeden zapis. `UrlSession` zapisuje stan przed usunięciem
lub przywróceniem; `UndoPreviewCommand` odtwarza go i zużywa historię.
Nowe generowanie i zmiana formularza czyszczą historię. Prototyp nadal służy
do przywracania całego pierwotnego zestawu, niezależnie od cofania ostatniej zmiany.

Szósty etap: **7. State** — `EmptyState`, `ReadyState` i `OpeningState` w pakiecie
`core.urls.states` określają dostępność edycji i przejścia otwierania.
`UrlSession` deleguje do bieżącego stanu; podczas otwierania odrzuca zmiany
i ponowny start. Widok blokuje formularz i akcje do zakończenia zadania,
zarówno po sukcesie, jak i błędzie. Historia cofania pozostaje zachowana.
Gotowość oznacza wygenerowany podgląd; jeśli usunięto wszystkie adresy,
otwieranie jest odrzucane, ale przywracanie i cofanie nadal działają.

Siódmy etap: **6. Observer** — `UrlSessionObserver` definiuje odbiorcę zmian.
Obserwowana sesja udostępnia kontrakt `UrlSessionObservable`. Klasa abstrakcyjna
`UrlSessionObservableSupport` zastępuje trait w Javie: przechowuje odbiorców,
obsługuje rejestrację, odpinanie i powiadamianie. `UrlSession` dziedziczy tę obsługę
i powiadamia po zmianie danych lub stanu. Obserwatorzy zależą od interfejsu sesji.
`UrlPreviewObserver` odświeża drzewo i licznik,
a `UrlActionsObserver` steruje dostępnością kontrolek. Każdy typ ma osobny plik;
model nie zależy od JavaFX. Powiadomienia są synchroniczne; widok zmienia sesję
na wątku JavaFX, także po zakończeniu otwierania. Odrzucone operacje nie powiadamiają
odbiorców, a ponowna rejestracja tego samego obiektu nie dubluje powiadomień.

## Laboratorium 3

**Punkt 11 — organizacja obiektowa i jeden typ w pliku.** Każdy plik Java w
`src/main/java` oraz `src/test/java` zawiera dokładnie jeden nazwany typ
(klasę, interfejs lub rekord). Nie ma klas zagnieżdżonych ani anonimowych.

- `JsonTool` zawiera sam kontrakt, a `JacksonJsonTool` i `TimedJsonTool`
  w pakiecie `core.json` odpowiadają za przetwarzanie JSON-a i pomiar czasu.
- `Processes` zawiera sam kontrakt. `SystemProcesses` w `infrastructure`
  obsługuje system operacyjny, a `GuardedProcesses` w `core.processes`
  kontroluje żądania zatrzymania procesu.
- `UrlNode` zawiera sam kontrakt drzewa. `UrlLink`, `UrlGroup` i `UrlBuilder`
  mają osobne pliki w `core.urls`. Osobny obiekt `UrlNodeFactory` wybiera
  rodzaj węzła; builder wywołuje jego metodę zamiast statycznej metody interfejsu.
- `RecordingProcesses` zastępuje anonimową implementację w teście proxy.

Fasada, widoki, sesja URL-i, polecenia, obserwatorzy i testy korzystają z nowego
układu. Zachowano dotychczasowe operacje oraz wzorce z laboratoriów 1 i 2.
Rekordy pozostają obiektami danych; lambdy obsługują zdarzenia i interfejsy
funkcyjne. Statyczna metoda `Launcher.main()` jest punktem wejścia aplikacji.

