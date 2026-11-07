package pl.edu.devtools.core.urls;

import java.net.URI;
import java.util.List;

/** Lab 3, zadanie 3: ISP, zastosowanie 4 — odczyt adresów bez obowiązku kopiowania drzewa. */
public interface UrlListing extends Iterable<URI> {
    List<URI> urls();
}
