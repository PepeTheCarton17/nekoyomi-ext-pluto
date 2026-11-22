package eu.kanade.tachiyomi.animeextension.es.pluto3

import eu.kanade.tachiyomi.animesource.model.AnimeFilter
import eu.kanade.tachiyomi.animesource.model.AnimeFilterList

object Pluto3Filters {
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
        PathFilter("Categoría (Latam)", arrayOf(Pair("Todos", ""), Pair("Shows Populares", "/latam/category/0fc98d49-648f-4d89-9c44-95e61107175f/"), Pair("Pelis Imperdibles", "/latam/category/6816a211-706c-4f86-9e3d-851144b7c274/"), Pair("Lo Más Visto", "/latam/category/61096f2f-b3a9-4f1b-aa99-9585e8942106/"))),
        PathFilter("Categoría (Live tv)", arrayOf(Pair("Todos", ""), Pair("Destacado", "/latam/watch/live-tv/category/featured/"), Pair("Películas", "/latam/watch/live-tv/category/peliculas-ve/"), Pair("Series", "/latam/watch/live-tv/category/series-ve/"), Pair("Retro", "/latam/watch/live-tv/category/retro-ve/"), Pair("Novelas", "/latam/watch/live-tv/category/novelas-ve/"), Pair("Reality", "/latam/watch/live-tv/category/reality-ve/"), Pair("Competencia", "/latam/watch/live-tv/category/competencia-ve/"), Pair("Curiosidad", "/latam/watch/live-tv/category/curiosidad-ve/"), Pair("Zona Paranormal", "/latam/watch/live-tv/category/paranormal-ve/"), Pair("Investigación", "/latam/watch/live-tv/category/investigacion-ve/"))),
        PathFilter("Categoría (Movies)", arrayOf(Pair("Todos", ""), Pair("Acción y Aventura", "/latam/movies/category/action-adventure/"), Pair("Comedia", "/latam/movies/category/comedy/"), Pair("Crimen", "/latam/movies/category/crime/"), Pair("Documentales", "/latam/movies/category/documentary/"), Pair("Drama", "/latam/movies/category/drama/"), Pair("Terror", "/latam/movies/category/horror/"), Pair("Niños y Familia", "/latam/movies/category/kids-family/"), Pair("Romance", "/latam/movies/category/romance/"), Pair("Sci-Fi y Fantasía", "/latam/movies/category/sci-fi-fantasy/"))),
        PathFilter("Categoría (Shows)", arrayOf(Pair("Todos", ""), Pair("Acción y Aventura", "/latam/shows/category/action-adventure/"), Pair("Comedia", "/latam/shows/category/comedy/"), Pair("Crimen", "/latam/shows/category/crime/"), Pair("Documentales", "/latam/shows/category/documentary/"), Pair("Drama", "/latam/shows/category/drama/"), Pair("Niños y Familia", "/latam/shows/category/kids-family/"), Pair("Estilo de Vida", "/latam/shows/category/lifestyle/"), Pair("Reality", "/latam/shows/category/reality/"), Pair("Deportes", "/latam/shows/category/sports/"))),
    )
}
