package eu.kanade.tachiyomi.animeextension.es.frame

import eu.kanade.tachiyomi.animesource.model.AnimeFilter
import eu.kanade.tachiyomi.animesource.model.AnimeFilterList

object FrameFilters {
    class Header(name: String) : AnimeFilter.Header(name)

    class ParamSelectFilter(name: String, val param: String, val options: Array<String>) :
        AnimeFilter.Select<String>(name, options)

    class ParamCheckFilter(name: String, val param: String, val value: String) :
        AnimeFilter.CheckBox(name, false)

    fun getFilterList(): AnimeFilterList = AnimeFilterList(
        Header("Filtros de búsqueda y catálogo"),
        ParamCheckFilter("Ko'proq ko'rsatish", "isFrameDub", "true"),
    )
}
