# DevTools

DevTools to nasz projekt grupowy na przedmiot
[Wzorce projektowe i techniki pisania czystego kodu](https://moodle3.cs.pollub.pl/course/view.php?id=1917).
Tworzymy aplikację w Javie z interfejsem JavaFX. Chcemy zebrać w jednym miejscu
kilka narzędzi, które przydadzą nam się podczas pracy nad projektami.

## Nasz plan

- Sprawdzanie poprawności JSON-a, formatowanie go i usuwanie zbędnych odstępów.
- Generowanie i otwieranie wielu URL-i na podstawie szablonu i listy wartości.
- Tworzenie profili, które łączą kilka akcji do wspólnego uruchomienia.
- Przeglądanie procesów i wysyłanie żądania zatrzymania wybranego procesu.
- Wyświetlanie wyników i czasu wykonania akcji oraz eksport raportów do pliku lub schowka.

Planujemy zacząć od JSON-a, potem dodać URL-e i profile, a następnie obsługę
procesów i eksport raportów. Logikę narzędzi będziemy oddzielać od widoków,
żeby łatwiej było dzielić pracę w grupie i testować poszczególne części.

## Wzorce na lab 1

Wybraliśmy dziewięć wzorców konstrukcyjnych i strukturalnych:

| Wzorzec | Do czego go wykorzystamy |
| --- | --- |
| Simple Factory | Tworzenie odpowiedniej akcji JSON: walidacja, formatowanie lub minifikacja. |
| Builder | Budowanie profilu z ustawień i wybranych akcji. |
| Prototype | Kopiowanie profilu razem z jego akcjami. |
| Adapter | Połączenie naszych interfejsów z API Javy do obsługi przeglądarki i procesów. |
| Composite | Łączenie pojedynczych akcji i grup akcji w profil. |
| Decorator | Dodanie pomiaru czasu do akcji i grup. |
| Facade | Prosta obsługa zapisywania i uruchamiania profili. |
| Proxy | Kontrola żądania zatrzymania procesu, np. blokowanie zatrzymania DevTools. |
| Bridge | Niezależny wybór formatu raportu i miejsca jego zapisu. |

Szczegółowy plan, propozycje klas i sposób sprawdzenia działania opisaliśmy
w [docs/lab 1.md](<docs/lab 1.md>).

## Obecny stan

Mamy konfigurację Maven oraz okno JavaFX z zakładkami Processes, JSON, URLs
i Profiles. UI jest podzielone na osobne pliki. Przyciski nie mają jeszcze
podłączonej logiki narzędzi, a dziewięć wybranych wzorców jest na etapie planu.

Obecna fabryka widoku porządkuje tworzenie UI. Nie traktujemy jej jeszcze
jako gotowego przykładu Simple Factory lub Factory Method na zaliczenie.

## Uruchomienie

Potrzebujemy JDK 17 i Maven 3.9+. Z katalogu projektu uruchamiamy:

```bash
mvn javafx:run
```

W IntelliJ IDEA importujemy projekt jako Maven i uruchamiamy klasę
`pl.edu.devtools.Launcher`. JavaFX uruchamiamy przez `Launcher`, a nie
bezpośrednio przez `DevToolsApplication`.

## Układ projektu

- `DevToolsApplication` - uruchomienie JavaFX i głównego okna.
- `ui` - nagłówek, fabryka widoku i układ aplikacji.
- `ui/tabs` - osobne klasy dla zakładek.
- `ui/model` - dane używane przez widoki.
- `docs` - nasze plany i opisy kolejnych laboratoriów.

## Testy

Gdy dodamy testy, będziemy uruchamiać je poleceniem:

```bash
mvn test
```
