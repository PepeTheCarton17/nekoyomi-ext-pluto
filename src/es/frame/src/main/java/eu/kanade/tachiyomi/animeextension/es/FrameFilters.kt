package eu.kanade.tachiyomi.animeextension.es.frame

import eu.kanade.tachiyomi.animesource.model.AnimeFilter
import eu.kanade.tachiyomi.animesource.model.AnimeFilterList

object FrameFilters {
    class Header(name: String) : AnimeFilter.Header(name)

    fun getFilterList(): AnimeFilterList = AnimeFilterList(
        Header("Filtros de búsqueda y catálogo"),
    )
}
