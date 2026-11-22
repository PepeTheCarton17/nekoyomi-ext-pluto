package eu.kanade.tachiyomi.animeextension.es.tube

import eu.kanade.tachiyomi.animesource.model.AnimeFilter
import eu.kanade.tachiyomi.animesource.model.AnimeFilterList

object TubeFilters {
    class Header(name: String) : AnimeFilter.Header(name)

    open class QueryPartFilter(displayName: String, val param: String, val vals: Array<Pair<String, String>>) :
        AnimeFilter.Select<String>(displayName, vals.map { it.first }.toTypedArray()) {
        fun toQueryPart(): String = vals[state].second
    }

    open class CheckBoxFilterList(name: String, val param: String, val pairs: Array<Pair<String, String>>) :
        AnimeFilter.Group<AnimeFilter.CheckBox>(name, pairs.map { CheckBoxVal(it.first, false) })

    class CheckBoxVal(name: String, state: Boolean = false) : AnimeFilter.CheckBox(name, state)

    class TextFilter(name: String, val param: String) : AnimeFilter.Text(name)

    open class PathFilter(displayName: String, val vals: Array<Pair<String, String>>) :
        AnimeFilter.Select<String>(displayName, vals.map { it.first }.toTypedArray()) {
        fun toPath(): String = vals[state].second
    }

    fun getFilterList(): AnimeFilterList = AnimeFilterList(
        Header("La búsqueda por texto ignora el filtro"),
        QueryPartFilter("U", "u", arrayOf(Pair("Todos", ""), Pair("Https://www.youtube.com/howyoutubeworks?utm Source=ythp&utm Medium=Left Nav&utm Campaign=ytgen", "https://www.youtube.com/howyoutubeworks?utm_source=ythp&utm_medium=LeftNav&utm_campaign=ytgen"))),
        TextFilter("", "id"),
        TextFilter("Parent", "parentId"),
        TextFilter("Previous", "previousId"),
        TextFilter("Next", "nextId"),
        TextFilter("Calidad", "quality"),
        TextFilter("By", "by"),
        TextFilter("Slot", "slotId"),
        TextFilter("Layout", "layoutId"),
        TextFilter("Triggeringlayout", "triggeringLayoutId"),
        TextFilter("Triggeringslot", "triggeringSlotId"),
        QueryPartFilter("Tipo", "type", arrayOf(Pair("Todos", ""), Pair("Error", "error"), Pair("Dv", "dv"), Pair("Fetch", "fetch"))),
        TextFilter("Tipo", "types"),
        TextFilter("Loading Response Message", "loading_response_message_id"),
        TextFilter("Ai Search Loading Response Message", "ai_search_loading_response_message_id"),
        TextFilter("Isslimgr", "isSlimGrid"),
        TextFilter("Isshortsgr", "isShortsGrid"),
        TextFilter("Video", "video_id"),
        CheckBoxFilterList("Formato", "format", arrayOf(Pair("Json", "json"))),
        TextFilter("Ad", "ad_id"),
        TextFilter("Visualización", "display"),
        TextFilter("Gs", "gs_id"),
    )
}
