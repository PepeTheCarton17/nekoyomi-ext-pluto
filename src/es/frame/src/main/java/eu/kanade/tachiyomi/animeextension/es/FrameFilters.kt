package eu.kanade.tachiyomi.animeextension.es.frame

import eu.kanade.tachiyomi.animesource.model.AnimeFilter
import eu.kanade.tachiyomi.animesource.model.AnimeFilterList

object FrameFilters {
    class Header(name: String) : AnimeFilter.Header(name)

    class Genre(val name: String, val id: String)
    class GenreFilter(name: String, val genres: List<Genre>) :
        AnimeFilter.Select<String>(name, genres.map { it.name }.toTypedArray())

    val DEFAULT_GENRES = listOf(
        Genre("Acción", "accion"),
        Genre("Aventura", "aventura"),
        Genre("Ciencia Ficción", "ciencia-ficcion"),
        Genre("Comedia", "comedia"),
        Genre("Deportes", "deportes"),
        Genre("Drama", "drama"),
        Genre("Ecchi", "ecchi"),
        Genre("Fantasía", "fantasia"),
        Genre("Gore", "gore"),
        Genre("Harem", "harem"),
        Genre("Isekai", "isekai"),
        Genre("Mecha", "mecha"),
        Genre("Romance", "romance"),
        Genre("Sci-Fi", "sci-fi"),
        Genre("Shounen", "shounen"),
        Genre("Sobrenatural", "sobrenatural"),
        Genre("Terror", "terror")
    )

    fun getFilterList(): AnimeFilterList = AnimeFilterList(
        Header("Filtros de búsqueda y catálogo"),
        GenreFilter("Género (#)", DEFAULT_GENRES)
    )
}
