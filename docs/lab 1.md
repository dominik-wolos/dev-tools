# Lab 1 - Wzorce konstrukcyjne i strukturalne

Przedmiot: [Wzorce projektowe i techniki pisania czystego kodu](https://moodle3.cs.pollub.pl/course/view.php?id=1917).

## Co chcemy zrobić

Aplikację z kilkoma narzędziami przydatnymi podczas programowania:

- Sprawdzanie JSON-a, formatowanie go i usuwanie zbędnych spacji.
- Otwieranie wielu stron z listy wartości, np. numerów zadań.
- Tworzenie profili, które zapisują kilka takich czynności do wspólnego uruchomienia.
- Wyświetlanie procesów i wysyłanie żądania zatrzymania wybranego procesu.
- Zapisywanie wyników wykonania profilu do pliku albo schowka.

Na lab 1 planujemy dziewięć wzorców z podanej listy.

## Co jest już zrobione

Jest konfiguracja Maven i okno JavaFX z czterema zakładkami.
UI jest podzielone na osobne pliki. Przyciski nie mają jeszcze logiki narzędzi.

Tworzenie okna jest przeniesione do fabryki widoku.
`AppViewFactory` określa metodę `createRoot()`, a `DefaultAppViewFactory`
tworzy widoki. `DevToolsApplication` korzysta z gotowego wyniku.

To na razie fabryka UI, której nie zaliczamy jako gotowej Simple Factory
ani Factory Method. Planowana Simple Factory będzie wybierać różne akcje,
np. walidację albo formatowanie JSON-a. Dziewięć wzorców z listy poniżej
nie jest jeszcze zaimplementowanych.

## Jakie wzorce planujemy

1. **Simple Factory** - `JsonActionFactory.create()` dostanie rodzaj operacji
   i utworzy `ValidateJsonAction`, `FormatJsonAction` albo `MinifyJsonAction`.
   Wszystkie akcje będą miały wspólny interfejs `JsonAction` z metodą `execute()`.
   Przyciski będą korzystać z tego interfejsu.
2. **Builder** - składanie profilu krok po kroku: nazwa, opis, ustawienia URL-i
   i wybrane akcje. Metoda `build()` sprawdzi wymagane dane i utworzy gotowy profil.
   Zajmie się tym `WorkspaceProfileBuilder`.
3. **Prototype** - `WorkspaceProfile.copy()` skopiuje gotowy profil razem
   z całym drzewem akcji i nada kopii nowe ID. Przycisk Duplicate użyje tej metody.
   Zmiana kopii nie będzie zmieniać oryginału.
4. **Adapter** - `DesktopBrowserAdapter` połączy interfejs `BrowserLauncher`
   z `java.awt.Desktop`. `JvmProcessAdapter` połączy `ProcessReader` i
   `ProcessStopper` z `ProcessHandle`. To dwa zastosowania jednego wzorca.
5. **Composite** - pojedyncza akcja i `ActionGroup` będą miały wspólny interfejs
   `WorkspaceAction`. Grupy będą mogły zawierać inne grupy. Na przykład profil
   będzie miał grupę JSON i grupę URL, a ta druga kilka akcji otwierania stron.
6. **Decorator** - `TimedActionDecorator` opakuje dowolną `WorkspaceAction`
   i doda pomiar czasu wykonania bez zmieniania samej akcji. Wynik i czas
   pokażemy w raporcie. Będzie można mierzyć też czas całej grupy.
7. **Facade** - `WorkspaceFacade` udostępni metody do zapisania i uruchomienia
   profilu. Połączy generator URL-i, fabrykę akcji, builder i repozytorium.
   Obsługa zakładki Profiles będzie korzystać z interfejsu `ProfileService`.
8. **Proxy** - `GuardedProcessStopper` będzie miał ten sam interfejs co zwykły
   `ProcessStopper`. Przed przekazaniem żądania sprawdzi PID. Zablokuje próbę
   zatrzymania samego DevTools, a pozostałe żądania przekaże do właściwej usługi.
9. **Bridge** - eksport raportu rozdzielimy na format i miejsce zapisu.
   `ReportExporter` będzie korzystał z interfejsu `ReportDestination`.
   `TextReportExporter` i `JsonReportExporter` przygotują treść, a
   `FileReportDestination` i `ClipboardReportDestination` ją zapiszą.
   Dzięki temu oba formaty będą działać zarówno z plikiem, jak i schowkiem.

## Kolejność pracy

1. Działające operacje JSON: Simple Factory i Decorator.
2. Generowanie URL-i i otwieranie przeglądarki: Adapter.
3. Tworzenie, kopiowanie i uruchamianie profili: Builder, Prototype, Composite i Facade.
4. Lista procesów i blokowanie zatrzymania DevTools: Adapter i Proxy.
5. Eksport raportów do pliku i schowka: Bridge.

`JsonAction` będzie wykonywać operację na podanym tekście. Akcja JSON w profilu
będzie przechowywać ten tekst i wybraną operację jako `WorkspaceAction`.

## Komentarze w kodzie

Fabryka widoku ma komentarze z numerem tygodnia, tytułem zajęć,
krótkim opisem i oznaczeniem końca. Obecne oznaczenie `Factory 1`
dotyczy organizacji UI. Po dodaniu właściwej Simple Factory przeniesiemy
oznaczenie wzorca do jej implementacji i miejsc użycia.

| Plik | Metoda lub element | Linie |
| --- | --- | --- |
| `ui/AppViewFactory.java` | Interfejs fabryki i `createRoot()` | 5-10 |
| `ui/DefaultAppViewFactory.java` | Klasa fabryki, `createRoot()` i `createTabs()` | 12-31 |
| `DevToolsApplication.java` | Wywołanie fabryki w `start()` | 24-27 |

Ścieżki w tabeli są względem `src/main/java/pl/edu/devtools/`.
Tak samo oznaczymy kolejne wzorce, kiedy zostaną dodane do projektu.
Same komentarze nie zastępują implementacji dziewięciu wzorców.

## Jak sprawdzimy działanie

- W fabryce sprawdzimy wszystkie trzy rodzaje operacji JSON.
- W builderze sprawdzimy poprawny profil i brak wymaganych danych.
- Zduplikujemy profil, zmienimy kopię i sprawdzimy, czy oryginał pozostał taki sam.
- Uruchomimy profil z zagnieżdżonymi grupami i pokażemy wyniki oraz czasy akcji.
- Sprawdzimy cały profil przez fasadę, z podstawioną przeglądarką.
- Sprawdzimy, czy proxy blokuje PID DevTools i przekazuje inne żądania do usługi.
- Wyeksportujemy raport tekstowy i JSON do pliku oraz schowka: cztery kombinacje.

Testy otwierania stron i zatrzymywania procesów użyją podstawionych usług.
Podczas testów nie będą otwierane prawdziwe karty ani zatrzymywane inne programy.

Logikę narzędzi będziemy trzymać poza widokami. Każda klasa ma mieć jedno zadanie,
a zależności będą przekazywane przez konstruktor.

Materiały: [Refactoring.Guru](https://refactoring.guru/pl/design-patterns),
[Simple Factory](https://refactoring.guru/design-patterns/factory-comparison),
[Bridge](https://refactoring.guru/pl/design-patterns/bridge),
[Proxy](https://refactoring.guru/pl/design-patterns/proxy).
