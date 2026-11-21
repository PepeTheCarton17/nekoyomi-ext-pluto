package eu.kanade.tachiyomi.animeextension.es.pluto3

import eu.kanade.tachiyomi.animesource.model.AnimeFilter
import eu.kanade.tachiyomi.animesource.model.AnimeFilterList

object Pluto3Filters {
    class Header(name: String) : AnimeFilter.Header(name)

    fun getFilterList(): AnimeFilterList = AnimeFilterList(
        Header("La búsqueda por texto ignora el filtro"),
    )
}
