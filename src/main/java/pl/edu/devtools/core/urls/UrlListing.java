package pl.edu.devtools.core.urls;

import java.net.URI;
import java.util.List;

/** Tydzień 3, punkt 3: ISP — odczyt adresów bez obowiązku kopiowania drzewa. */
public interface UrlListing extends Iterable<URI> {
    List<URI> urls();
}
