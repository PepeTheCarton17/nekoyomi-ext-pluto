package eu.kanade.tachiyomi.animeextension.all.frame

import eu.kanade.tachiyomi.animesource.model.AnimeFilter
import eu.kanade.tachiyomi.animesource.model.AnimeFilterList

object FrameFilters {
    class Header(name: String) : AnimeFilter.Header(name)

    open class QueryPartFilter(displayName: String, val param: String, val vals: Array<Pair<String, String>>) :
        AnimeFilter.Select<String>(displayName, vals.map { it.first }.toTypedArray()) {
        fun toQueryPart(): String = vals[state].second
    }

    open class CheckBoxFilterList(name: String, val param: String, val pairs: Array<Pair<String, String>>) :
        AnimeFilter.Group<AnimeFilter.CheckBox>(name, pairs.map { CheckBoxVal(it.first, false) })

    class CheckBoxVal(name: String, state: Boolean = false) : AnimeFilter.CheckBox(name, state)

    class TextFilter(name: String, val param: String) : AnimeFilter.Text(name)

    fun getFilterList(): AnimeFilterList = AnimeFilterList(
        Header("Search & Catalog Filters"),
        QueryPartFilter("Dubbed", "isFrameDub", arrayOf(Pair("All", ""), Pair("True", "true"))),
        TextFilter("Search", "query"),
        TextFilter("Search", "search"),
    )
}
