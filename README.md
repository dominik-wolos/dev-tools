# DevTools

Aplikacja Java 17 + JavaFX na zajęcia
[Wzorce projektowe i techniki pisania czystego kodu](https://moodle3.cs.pollub.pl/course/view.php?id=1712).

## Funkcje

- **Processes** — lista uruchomionych procesów, sortowanie, filtr nazwy/PID/użytkownika,
  odświeżanie, ścieżka programu, czas uruchomienia i łączny czas CPU.
  Zatrzymanie wymaga potwierdzenia; proxy blokuje własny PID oraz PID 1.
  Brak uprawnień systemowych do szczegółów procesu jest oznaczany w tabeli.
- **JSON** — walidacja i formatowanie przez Jacksona, opis błędu z wierszem/kolumną,
  pomiar czasu. Walidacja odrzuca pusty tekst i dodatkowe dane po dokumencie.
- **URLs** — podgląd i otwieranie adresów z szablonu z jednym nazwanym parametrem,
  np. `localhost/orders/{orderID}`. Każdy znak poza cyframi `0–9` jest separatorem.
  Lista `1, 2;003 | 4` tworzy cztery adresy. Kolejność, powtórzenia i zera wiodące
  są zachowywane; brak protokołu oznacza `http://`.
  Można usunąć URL lub całą grupę z podglądu, a następnie przywrócić pierwotny zestaw.
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
Zaimplementowane wzorce mają stałe numery 1–9, używane także w komentarzach kodu:

1. **Simple Factory**
2. **Builder**
3. **Prototype**
4. **Adapter**
5. **Composite**
6. **Decorator**
7. **Facade**
8. **Proxy**
9. **Bridge**
