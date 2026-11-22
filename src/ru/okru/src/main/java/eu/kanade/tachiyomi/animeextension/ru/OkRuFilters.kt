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
        QueryPartFilter("St.cmd", "st.cmd", arrayOf(Pair("Todos", ""), Pair("Anonym Main", "anonymMain"), Pair("Help Faq", "helpFaq"), Pair("Log External", "logExternal"), Pair("Help Agreement Page", "helpAgreementPage"), Pair("Anonym Video", "anonymVideo"), Pair("Anonym Registration Enter Phone", "anonymRegistrationEnterPhone"), Pair("Anonym Global Search", "anonymGlobalSearch"))),
        QueryPartFilter("St.origin", "st.origin", arrayOf(Pair("Todos", ""), Pair("FTRL", "FTRL"))),
        QueryPartFilter("St. A", "st._aid", arrayOf(Pair("Todos", ""), Pair("Fat Footer Help Faq Anonym", "FatFooter_helpFaqAnonym"), Pair("Fat Footer Official Group", "FatFooter_officialGroup"), Pair("Video State Open Showcase", "VideoState_open_showcase"))),
        QueryPartFilter("Cmd", "cmd", arrayOf(Pair("Todos", ""), Pair("Log External", "logExternal"), Pair("Video Card Menu", "VideoCardMenu"))),
        QueryPartFilter("St.sig", "st.sig", arrayOf(Pair("Todos", ""), Pair("Y2w966 K9qtnimbuggih ZT0Oo GJv5EBY Tv Xa1u5c Tfs Gq WZ3LKi T38Bw Rm Col8Mm Gwh FUTXx3p Vvy Mw", "Y2w966_K9qtnimbuggihZT0OoGJv5EBY-TvXa1u5cTfsGq_WZ3LKiT38BwRmCol8Mm_gwhFUTXx3pVvyMw"))),
        QueryPartFilter("St.link", "st.link", arrayOf(Pair("Todos", ""), Pair("Http://www.translit.ru", "http://www.translit.ru"))),
        QueryPartFilter("St.name", "st.name", arrayOf(Pair("Todos", ""), Pair("Translit Ru", "translit_ru"))),
        QueryPartFilter("St.vc.movie", "st.vc.movieId", arrayOf(Pair("Todos", ""), Pair("16673511967349", "16673511967349"), Pair("7475662490142", "7475662490142"), Pair("18119981402657", "18119981402657"), Pair("16747694525165", "16747694525165"), Pair("17160742439534", "17160742439534"), Pair("18508365957760", "18508365957760"), Pair("16169403681298", "16169403681298"), Pair("17955108948706", "17955108948706"), Pair("16768288295665", "16768288295665"), Pair("16169365801490", "16169365801490"), Pair("17778749213250", "17778749213250"), Pair("18328050797184", "18328050797184"), Pair("16698275531402", "16698275531402"), Pair("17304329456226", "17304329456226"), Pair("16634009029130", "16634009029130"), Pair("16559703198454", "16559703198454"), Pair("17590609316576", "17590609316576"), Pair("17692702804647", "17692702804647"))),
        QueryPartFilter("St.vc.action", "st.vc.action", arrayOf(Pair("Todos", ""), Pair("RESTORE", "RESTORE"))),
        QueryPartFilter("St.m", "st.m", arrayOf(Pair("Todos", ""), Pair("SHOWCASE", "SHOWCASE"))),
        QueryPartFilter("St.ft", "st.ft", arrayOf(Pair("Todos", ""), Pair("Showcase", "showcase"))),
        QueryPartFilter("St.furl", "st.furl", arrayOf(Pair("Todos", ""), Pair("/video/showcase", "/video/showcase"))),
    )
}
