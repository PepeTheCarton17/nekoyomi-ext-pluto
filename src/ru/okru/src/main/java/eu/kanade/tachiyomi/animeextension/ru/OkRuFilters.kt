package eu.kanade.tachiyomi.animeextension.ru.okru

import eu.kanade.tachiyomi.animesource.model.AnimeFilter
import eu.kanade.tachiyomi.animesource.model.AnimeFilterList

object OkRuFilters {
    class Header(name: String) : AnimeFilter.Header(name)

    // La lista negra del panel invierte el término: no lo busca, lo excluye.
    // Se inicializa con el valor del ajuste para que ambos sean un mismo espejo.
    class BlacklistInputFilter(name: String, initial: String) : AnimeFilter.Text(name) {
        init { state = initial }
    }

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

    fun getFilterList(initialBlacklist: String = ""): AnimeFilterList = AnimeFilterList(
        Header("La búsqueda por texto ignora el filtro"),
        BlacklistInputFilter("Lista Negra - excluir (separar con , o ;)", initialBlacklist),
        QueryPartFilter("St.cmd", "st.cmd", arrayOf(Pair("Todos", ""), Pair("Anonym Main", "anonymMain"), Pair("Help Faq", "helpFaq"), Pair("Log External", "logExternal"), Pair("Help Agreement Page", "helpAgreementPage"), Pair("Anonym Registration Enter Phone", "anonymRegistrationEnterPhone"), Pair("Anonym Global Search", "anonymGlobalSearch"))),
        QueryPartFilter("St.origin", "st.origin", arrayOf(Pair("Todos", ""), Pair("FTRL", "FTRL"))),
        QueryPartFilter("St. A", "st._aid", arrayOf(Pair("Todos", ""), Pair("Fat Footer Help Faq Anonym", "FatFooter_helpFaqAnonym"), Pair("Fat Footer Official Group", "FatFooter_officialGroup"))),
        QueryPartFilter("Cmd", "cmd", arrayOf(Pair("Todos", ""), Pair("Log External", "logExternal"), Pair("Pop Layer", "PopLayer"))),
        QueryPartFilter("St.sig", "st.sig", arrayOf(Pair("Todos", ""), Pair("Y2w966 K9qt3u UCYPFRl4zrl Eg Ry Ampo QH Qb DLraei X8Sh90Bxwglt2IIo Kq4Be3N7q13QSre Ru RE Lg", "Y2w966_K9qt3uUCYPFRl4zrlEgRyAmpoQH-qbDLraeiX8Sh90Bxwglt2IIo-kq4Be3N7q13QSreRuRE-lg"))),
        QueryPartFilter("St.link", "st.link", arrayOf(Pair("Todos", ""), Pair("Http://www.translit.ru", "http://www.translit.ru"))),
        QueryPartFilter("St.name", "st.name", arrayOf(Pair("Todos", ""), Pair("Translit Ru", "translit_ru"))),
        QueryPartFilter("St.layer.cmd", "st.layer.cmd", arrayOf(Pair("Todos", ""), Pair("Pop Layer Choose New Language", "PopLayerChooseNewLanguage"))),
    )
}
