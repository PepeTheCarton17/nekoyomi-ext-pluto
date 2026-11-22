package eu.kanade.tachiyomi.animeextension.en.frame

import android.app.Application
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.MultiSelectListPreference
import androidx.preference.PreferenceScreen
import eu.kanade.tachiyomi.animesource.ConfigurableAnimeSource
import eu.kanade.tachiyomi.animesource.model.AnimeFilterList
import eu.kanade.tachiyomi.animesource.model.AnimesPage
import eu.kanade.tachiyomi.animesource.model.SAnime
import eu.kanade.tachiyomi.animesource.model.SEpisode
import eu.kanade.tachiyomi.animesource.model.Video
import eu.kanade.tachiyomi.animesource.online.ParsedAnimeHttpSource
import eu.kanade.tachiyomi.network.GET
import eu.kanade.tachiyomi.network.POST
import eu.kanade.tachiyomi.util.asJsoup
import okhttp3.Headers
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.Jsoup
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class Frame : ParsedAnimeHttpSource(), ConfigurableAnimeSource {

    override val name = "Frame"

    override val baseUrl = "https://frame.uz"

    override val lang = "en"

    override val supportsLatest = false

    override val client: OkHttpClient = network.cloudflareClient

    // Rutas detectadas durante el análisis (vacías si no se encontraron).
    private val listingPath = "/catalog"
    private val latestPath = ""
    private val searchPath = "/catalog"
    private val searchParam = "name"
    private val pageLanguage = "uz"

    // Títulos reales declarados por el sitio con schema.org ItemList. Se usan
    // sólo cuando el HTML no expone tarjetas (SPA); nunca se inventan títulos.
    private data class LdRecord(
        val title: String,
        val alternateTitle: String?,
        val language: String?,
        val url: String,
        val image: String?,
        val genre: String? = null
    )

    private val jsonLdRecords = listOf(
        LdRecord(title = "Newcomers", alternateTitle = null, language = null, url = "https://frame.uz/cinema/ponaekhavshie", image = "https://st.frame.uz/files/contents/57533/poster/f3165bd4-d7e2-4c1e-9a83-cbc1c0b2a51a_En.webp", genre = "Series"),
        LdRecord(title = "Heavy luxry", alternateTitle = null, language = null, url = "https://frame.uz/cinema/tyazhyolyy-lyuks", image = "https://st.frame.uz/files/contents/57504/poster/4366779f-853d-43dd-9f70-71a80da27da6_En.webp", genre = "Series"),
        LdRecord(title = "AInga", alternateTitle = null, language = null, url = "https://frame.uz/cinema/iinga", image = "https://st.frame.uz/files/contents/57340/poster/cc0d5ec6-fe62-4ab2-9494-4e9f1c03e7ed_En.webp", genre = "Series"),
        LdRecord(title = "Underground  stand-up comedy", alternateTitle = null, language = null, url = "https://frame.uz/cinema/stendap-andegraund", image = "https://st.frame.uz/files/contents/57086/poster/bb45be08-a003-49f0-a730-67354dbc7267_En.webp", genre = "TV show"),
        LdRecord(title = "Co-authors", alternateTitle = null, language = null, url = "https://frame.uz/cinema/soavtory", image = "https://st.frame.uz/files/contents/56995/poster/9d2eef09-c54b-45c4-9567-1237142119ac_En.webp", genre = "Movies"),
        LdRecord(title = "At the wheel of destiny", alternateTitle = null, language = null, url = "https://frame.uz/cinema/za-rulem-sudby", image = "https://st.frame.uz/files/contents/56971/poster/0494b1a9-02ac-4c1f-84d7-72d2b0682869_En.webp", genre = "Series"),
        LdRecord(title = "Ilya Murovets", alternateTitle = null, language = null, url = "https://frame.uz/cinema/ilya-murovets", image = "https://st.frame.uz/files/contents/56944/poster/90a2be5e-51ba-478b-95ea-8a247c823fde_En.webp", genre = "Series"),
        LdRecord(title = "Qora quzg'un", alternateTitle = null, language = null, url = "https://frame.uz/cinema/qora-quzg-un", image = "https://st.frame.uz/files/contents/56977/poster/915132f0-022d-430e-a664-04a475cdc2d1_En.webp", genre = "Movies"),
        LdRecord(title = "Betayin", alternateTitle = null, language = null, url = "https://frame.uz/cinema/betayin", image = "https://st.frame.uz/files/contents/56976/poster/422d796e-65fa-45ea-9ea7-eadd1f301520_En.webp", genre = "Movies"),
        LdRecord(title = "Red Queen", alternateTitle = null, language = null, url = "https://frame.uz/cinema/krasnaya-koroleva", image = "https://st.frame.uz/files/contents/56921/poster/17dcd446-191f-4234-8cc4-6484152b88fd_En.webp", genre = "Series"),
        LdRecord(title = "Imperator rafiqasi Ki", alternateTitle = null, language = null, url = "https://frame.uz/cinema/imperatrica-ki", image = "https://st.frame.uz/files/contents/7291/poster/e6d4c866-5a89-4fab-82d1-b562b3ffbfcc_Uz.webp"),
        LdRecord(title = "1+1 qo'shilish kuchi", alternateTitle = null, language = null, url = "https://frame.uz/cinema/intouchables", image = "https://st.frame.uz/files/contents/298/poster/b8c331bf-1141-4344-97da-9343eaf2fe9b_Uz.webp"),
        LdRecord(title = "Ogi‘r lyuks", alternateTitle = null, language = null, url = "https://frame.uz/cinema/tyazhyolyy-lyuks", image = "https://st.frame.uz/files/contents/57504/poster/8a622de6-1f14-4eae-a96b-ee0209bf7d9b_Uz.webp"),
        LdRecord(title = "Hammasiga Istanbul aybdor", alternateTitle = null, language = null, url = "https://frame.uz/cinema/vo-vsem-vinovat-stambul", image = "https://st.frame.uz/files/contents/45462/poster/3dc5649e-34fc-41d1-a2c9-68874b3d04fb_Uz.webp"),
        LdRecord(title = "Layk Nastya", alternateTitle = null, language = null, url = "https://frame.uz/cinema/layk-nastya", image = "https://st.frame.uz/files/contents/56457/poster/8c861331-d087-49e1-9685-d70ca373947c_Uz.webp"),
        LdRecord(title = "So`nggi zarba", alternateTitle = null, language = null, url = "https://frame.uz/cinema/so-nggi-zarba", image = "https://st.frame.uz/files/contents/56688/poster/d5776654-f7f4-482b-9fef-ecc2a6629a8a_Uz.webp"),
        LdRecord(title = "Yoshlar", alternateTitle = null, language = null, url = "https://frame.uz/cinema/molodezka", image = "https://st.frame.uz/files/contents/23628/poster/36cd7ab3-26e3-4291-af62-9c95c6a46f41_Uz.webp"),
        LdRecord(title = "Farz", alternateTitle = null, language = null, url = "https://frame.uz/cinema/dolg", image = "https://st.frame.uz/files/contents/54827/poster/381e995d-a876-445d-b838-57e89f14cf25_Uz.webp"),
        LdRecord(title = "Fara Tashkentskiy", alternateTitle = null, language = null, url = "https://frame.uz/cinema/fara-taskentskij", image = "https://st.frame.uz/files/contents/5821/poster/04ee5bf5-e2e0-4c07-b3b0-755df83f225d_Uz.webp"),
        LdRecord(title = "Yigitning so‘zi. Asfaltdagi qon", alternateTitle = null, language = null, url = "https://frame.uz/cinema/slovo-pacana-krov-na-asfalte", image = "https://st.frame.uz/files/contents/2515/poster/4a16674b-6659-4b11-a0c8-ebc4db9f7aae_Uz.webp")
    )

    private fun recordToAnime(record: LdRecord): SAnime = SAnime.create().apply {
        setUrlWithoutDomain(record.url)
        title = preferredRecordTitle(record)
        thumbnail_url = record.image
        genre = record.genre
    }

    /** Idioma pedido > alternativa en inglés > nombre original del sitio. */
    private fun preferredRecordTitle(record: LdRecord): String {
        val alt = record.alternateTitle?.takeIf { it.isNotBlank() }
        return when {
            alt != null && (lang == "en" || record.language != lang) -> alt
            else -> record.title
        }
    }

    /** Prioriza atributos localizados reales; el texto visible manda si la página ya está en el idioma pedido. */
    private fun pickTitle(element: Element, titleSelector: String): String {
        val text = element.selectFirst(titleSelector)?.text()?.trim().orEmpty()
            .ifBlank { element.selectFirst("h2, h3, .title, [class*=title]")?.text()?.trim().orEmpty() }
        fun attr(name: String): String = element.attr(name).trim()
            .ifBlank { element.selectFirst("[$name]")?.attr(name)?.trim().orEmpty() }
        val attrs = if (lang == "en") {
            listOf("data-title-en", "title")
        } else {
            listOf("data-title-en", "data-title-en", "title")
        }
        if (pageLanguage == lang && text.isNotBlank()) return text
        for (name in attrs) {
            val value = attr(name)
            if (value.isNotBlank()) return value
        }
        return text.ifBlank { attr("alt") }.ifBlank { "Sin Título" }
    }

    /** Etiquetas reales de la tarjeta (género/categoría/tag), nunca inventadas. */
    private fun pickGenres(element: Element, genreSelector: String): String? {
        if (genreSelector.isBlank()) return null
        val values = runCatching {
            element.select(genreSelector)
                .map { it.text().trim() }
                .filter { it.isNotEmpty() && it.length <= 60 }
                .distinct()
        }.getOrDefault(emptyList<String>())
        return values.takeIf { it.isNotEmpty() }?.joinToString(", ")
    }

    private fun pageUrl(path: String, page: Int): String {
        val clean = if (path.isEmpty()) "/" else if (path.startsWith("/")) path else "/$path"
        val base = "$baseUrl$clean"
        return when ("none") {
            "path" -> if (page <= 1) base else "$baseUrl${clean.trimEnd('/')}/page/$page/"
            "query" -> if (page <= 1) base
                else "$base${if (base.contains("?")) "&" else "?"}page=$page"
            else -> base
        }
    }

    private fun searchUrl(query: String, page: Int): String {
        val path = if (searchPath.isEmpty()) "/" else if (searchPath.startsWith("/")) searchPath else "/$searchPath"
        if (searchPath.isEmpty() || searchParam.isEmpty()) {
            return pageUrl(listingPath, page)
        }
        val base = "$baseUrl$path"
        val sep = if (base.contains("?")) "&" else "?"
        val encoded = Uri.encode(query)
        val first = "$base$sep$searchParam=$encoded"
        return if (page <= 1) first else "$first&page=$page"
    }

    private val preferences: SharedPreferences by lazy {
        Injekt.get<Application>().getSharedPreferences("source_$id", 0)
    }

    override fun headersBuilder(): Headers.Builder = Headers.Builder()
        .add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36")
        .add("Referer", "$baseUrl/")

    // Un filtro de ruta es navegación de catálogo: popular/latest no reciben
    // el AnimeFilterList, así que leen la ruta elegida de las preferencias.
    private fun catalogPath(fallback: String): String =
        preferences.getString("selected_path_filter", "").orEmpty().ifBlank { fallback }

    // ============================== POPULAR ANIME ==============================
    override fun popularAnimeRequest(page: Int): Request = GET(pageUrl(catalogPath(listingPath), page), headers)

    override fun popularAnimeSelector(): String = "article, div.item, div.card"

    override fun popularAnimeFromElement(element: Element): SAnime = SAnime.create().apply {
        // En muchas portadas la tarjeta ES el enlace (`a.card`): `selectFirst`
        // solo busca descendientes, así que hay que aceptar el propio nodo.
        val link = if (element.tagName() == "a") element else element.selectFirst("a")
        setUrlWithoutDomain(link?.attr("href") ?: "")
        title = pickTitle(element, "h3 a, h2 a, .title a")
        genre = pickGenres(element, "")
        thumbnail_url = element.selectFirst("img")
            ?.let { img ->
                img.attr("abs:src")
                    .ifBlank { img.attr("abs:data-src") }
                    .ifBlank { img.attr("abs:data-lazy-src") }
            }
            ?.takeIf { it.isNotEmpty() }
    }

    override fun popularAnimeNextPageSelector(): String? = "".ifBlank { null }

    override fun popularAnimeParse(response: Response): AnimesPage {
        val page = filterBlacklistedAnimes(super.popularAnimeParse(response))
        return withJsonLdFallback(page, "")
    }

    private fun withJsonLdFallback(page: AnimesPage, query: String): AnimesPage {
        var animes = page.animes
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            animes = animes.filter { it.title.lowercase().contains(q) }
        }
        if (animes.isEmpty() && jsonLdRecords.isNotEmpty()) {
            animes = jsonLdRecords
                .map(::recordToAnime)
                .filter { anime -> query.isBlank() || anime.title.lowercase().contains(query.trim().lowercase()) }
            return AnimesPage(animes, false)
        }
        return AnimesPage(animes, page.hasNextPage)
    }

    // ============================== LATEST UPDATES ==============================
    override fun latestUpdatesRequest(page: Int): Request = GET(pageUrl(catalogPath(latestPath), page), headers)

    override fun latestUpdatesSelector(): String = "article, div.item, div.card"

    override fun latestUpdatesFromElement(element: Element): SAnime = popularAnimeFromElement(element)

    override fun latestUpdatesNextPageSelector(): String? = "".ifBlank { null }

    override fun latestUpdatesParse(response: Response): AnimesPage {
        val page = filterBlacklistedAnimes(super.latestUpdatesParse(response))
        return withJsonLdFallback(page, "")
    }

    // ============================== SEARCH ANIME ==============================
    override fun searchAnimeRequest(page: Int, query: String, filters: AnimeFilterList): Request {
        val cleanQuery = query.trim()
        val excludedApiValues = filters.filterIsInstance<FrameFilters.ExcludeFilter>()
            .filter { it.state }
            .map { it.value.lowercase() }
            .toHashSet()
        preferences.edit().putStringSet("excluded_api_values", excludedApiValues).apply()
        val blacklistInput = filters.filterIsInstance<FrameFilters.BlacklistInputFilter>()
            .firstOrNull()?.state.orEmpty()
        preferences.edit().putString("genre_blacklist_text", blacklistInput).apply()
        val finalQuery = cleanQuery

        val url = if (finalQuery.isNotBlank()) {
            searchUrl(finalQuery, page)
        } else {
            pageUrl(listingPath, page)
        }
        lastSearchQuery = finalQuery
        // Un filtro de ruta sustituye el path del catálogo; los query params
        // originales (p. ej. la paginación) se conservan sobre la nueva ruta.
        val pathOverride = filters.filterIsInstance<FrameFilters.PathFilter>()
            .firstOrNull()?.toPath().orEmpty()
        preferences.edit().putString("selected_path_filter", pathOverride).apply()
        val originalUrl = Uri.parse(url)
        val urlBuilder = if (pathOverride.isNotBlank()) {
            Uri.parse(baseUrl.trimEnd('/') + pathOverride).buildUpon().apply {
                originalUrl.queryParameterNames.forEach { name ->
                    originalUrl.getQueryParameter(name)?.let { appendQueryParameter(name, it) }
                }
            }
        } else {
            originalUrl.buildUpon()
        }
        for (f in filters) {
            when (f) {
                is FrameFilters.PathFilter -> Unit
                is FrameFilters.QueryPartFilter -> {
                    val value = f.toQueryPart()
                    if (value.isNotBlank()) urlBuilder.appendQueryParameter(f.param, value)
                }
                is FrameFilters.CheckBoxFilterList -> {
                    f.state.filter { it.state }.forEach { box ->
                        f.pairs.firstOrNull { it.first == box.name }?.second?.let { value ->
                            urlBuilder.appendQueryParameter(f.param, value)
                        }
                    }
                }
                is FrameFilters.TextFilter -> if (f.state.isNotBlank()) {
                    urlBuilder.appendQueryParameter(f.param, f.state)
                }
                else -> Unit
            }
        }
        val finalUrl = urlBuilder.build().toString()
        return GET(finalUrl, headers)
    }

    private var lastSearchQuery = ""

    override fun searchAnimeSelector(): String = "article, div.item, div.card"

    override fun searchAnimeFromElement(element: Element): SAnime = popularAnimeFromElement(element)

    override fun searchAnimeNextPageSelector(): String? = "".ifBlank { null }

    override fun searchAnimeParse(response: Response): AnimesPage {
        val page = filterBlacklistedAnimes(super.searchAnimeParse(response))
        // Sin endpoint de búsqueda, el sitio devuelve el catálogo sin filtrar.
        // Filtrar localmente es honesto: no fingimos que el servidor buscó.
        return withJsonLdFallback(page, lastSearchQuery)
    }

    private fun filterBlacklistedAnimes(page: AnimesPage): AnimesPage {
        val rawBlacklist = preferences.getString("genre_blacklist_text", "") ?: ""
        // El input del panel y el ajuste comparten clave: ambos admiten
        // términos separados por coma, punto y coma o barra vertical.
        val blacklistWords = rawBlacklist
            .split(",", ";", "|")
            .map { it.trim().lowercase() }
            .filter { it.isNotEmpty() }
        // Valores elegidos en el filtro de exclusión: ids/nombres reales de las
        // listas verificadas de la API del sitio, no palabras escritas a mano.
        val excludedApi = (preferences.getStringSet("excluded_api_values", mutableSetOf<String>()) ?: emptySet())
            .map { it.lowercase() }
            .filter { it.isNotBlank() }

        if (blacklistWords.isEmpty() && excludedApi.isEmpty()) return page

        val filtered = page.animes.filterNot { anime ->
            val genreLower = anime.genre?.lowercase() ?: ""
            val genreWords = genreLower.split(",", ";", "|")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
            val excludedHit = excludedApi.any { word ->
                genreWords.any { genre -> genre == word || genre.contains(word) }
            }
            val titleLower = anime.title.lowercase()
            excludedHit || blacklistWords.any { word ->
                (genreLower.isNotEmpty() && genreLower.contains(word)) || titleLower.contains(word)
            }
        }
        return AnimesPage(filtered, page.hasNextPage)
    }

    override fun getFilterList(): AnimeFilterList =
        FrameFilters.getFilterList(preferences.getString("genre_blacklist_text", "") ?: "")

    // ============================== DETAILS ==============================
    override fun animeDetailsParse(document: Document): SAnime = SAnime.create().apply {
        title = document.selectFirst("h1, .title, .entry-title")?.text()?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?: document.selectFirst("meta[property=og:title]")?.attr("content")
            ?: "Anime"
        description = document.selectFirst("p.sinopsis, div.description, .entry-content p")?.text()?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?: document.selectFirst("meta[name=description], meta[property=og:description]")?.attr("content")
        genre = document.select("a[href*=/genero/], a[href*=/genre/], .genres a").joinToString(", ") { it.text() }
        status = SAnime.UNKNOWN
        thumbnail_url = document.selectFirst("img.poster, .poster img")?.attr("abs:src")
            ?: document.selectFirst("meta[property=og:image]")?.attr("content")
            ?: document.selectFirst("article img, main img")?.attr("abs:src")
    }

    // ============================== EPISODES ==============================
    override fun episodeListSelector(): String = "li.episode, a[href*=/ver/], div.episodios a"

    override fun episodeFromElement(element: Element): SEpisode = SEpisode.create().apply {
        // La tarjeta puede ser el propio enlace.
        val link = if (element.tagName() == "a") element else element.selectFirst("a")
        setUrlWithoutDomain(link?.attr("href") ?: "")
        // Nombre: un título explícito o los dos primeros textos cortos. En
        // tarjetas con números de temporada el primero es "T1 E1" y el segundo
        // el título del episodio; así no se cuela la sinopsis completa.
        val explicitTitle = element.selectFirst(".title, [class*=episodeTitle], h3, h4")
            ?.text()?.trim()?.takeIf { it.isNotEmpty() }
        val shortTexts = element.select("span")
            .map { it.text().trim() }
            .filter { it.isNotEmpty() && it.length <= 60 }
            .take(2)
            .joinToString(" ")
        name = explicitTitle
            ?: shortTexts.takeIf { it.isNotEmpty() }
            ?: link?.text()?.trim()?.takeIf { it.isNotEmpty() }
            ?: "Episodio"
        val seasonEpisode = Regex("""[TS]\s*\d+\s*[Ee]\s*(\d+)""", RegexOption.IGNORE_CASE).find(name)
        val wordEpisode = Regex("""(?:episodio|capitulo|capítulo|ep)\s*[-#:]?\s*(\d+)""", RegexOption.IGNORE_CASE).find(name)
        episode_number = (seasonEpisode?.groupValues?.get(1) ?: wordEpisode?.groupValues?.get(1))
            ?.toFloatOrNull() ?: 1f
    }

    override fun videoFromElement(element: Element): Video = throw UnsupportedOperationException()
    override fun videoListSelector(): String = throw UnsupportedOperationException()
    override fun videoUrlParse(document: Document): String = throw UnsupportedOperationException()

    // Capacidades detectadas por AEXT en el análisis del sitio. No sustituyen
    // a la verificación real: sólo ordenan los candidatos a probar.
    private val siteStreamFormats = setOf<String>("mp4", "iframe")

    // ============================== VIDEO LIST & EXTRACTORS ==============================

    private val userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"

    // `(?![a-zA-Z])` evita falsos positivos como `300px`/`250px` del CSS.
    private val resolutionRegex = Regex("""(\d{3,4})p(?![a-zA-Z])""", RegexOption.IGNORE_CASE)

    private fun normalizeQuality(raw: String?): String? {
        val height = raw?.toIntOrNull() ?: return null
        return if (height in 144..4320) height.toString() + "p" else null
    }

    // ============================== VERIFICACIÓN DE STREAMS ==============================
    // Un HTTP 200 en el máster no garantiza que el reproductor pueda abrir el
    // stream: pueden fallar las cabeceras, la clave AES-128 o un segmento.
    // Comprobamos la cadena completa antes de entregar el Video al reproductor.
    private data class StreamProbe(
        val ok: Boolean,
        val detail: String,
        val size: Int = 0,
        val body: String = "",
        val inconclusive: Boolean = false,
        // Pistas reales declaradas por el máster HLS. Se usan para etiquetar
        // el vídeo con idiomas detectados, nunca con un DUB supuesto.
        val audioLangs: List<String> = emptyList(),
        val subLangs: List<String> = emptyList(),
    )

    /** Lee `#EXT-X-MEDIA` y devuelve (audio, subtítulos) con su idioma real. */
    private fun hlsTracks(body: String): Pair<List<String>, List<String>> {
        val audio = linkedSetOf<String>()
        val subs = linkedSetOf<String>()
        for (line in body.lineSequence()) {
            if (!line.startsWith("#EXT-X-MEDIA")) continue
            val type = Regex("""TYPE=([A-Z]+)""").find(line)?.groupValues?.get(1)?.uppercase() ?: continue
            val lang = Regex("""LANGUAGE="([^"]+)"""").find(line)?.groupValues?.get(1)?.trim().orEmpty()
            val name = Regex("""NAME="([^"]+)"""").find(line)?.groupValues?.get(1)?.trim().orEmpty()
            val code = lang.takeIf { it.isNotEmpty() && it != "und" } ?: name
            if (code.isEmpty()) continue
            if (type == "AUDIO") audio.add(code) else if (type == "SUBTITLES") subs.add(code)
        }
        return audio.toList() to subs.toList()
    }

    private val browserHeaders: Headers by lazy {
        Headers.Builder()
            .set("User-Agent", userAgent)
            .set("Referer", baseUrl)
            .set("Accept", "*/*")
            .set("Accept-Language", "es-ES,es;q=0.9,en;q=0.8")
            .build()
    }

    private val hlsKeyRegex = Regex("#EXT-X-KEY[^\\r\\n]*URI=\"([^\"]+)\"")

    private fun isPlaylist(url: String): Boolean =
        url.substringBefore("?").endsWith(".m3u8", ignoreCase = true)

    private fun probeOnce(url: String, extra: Headers, readBody: Boolean = false): StreamProbe = try {
        val request = Request.Builder().url(url).headers(extra).header("Range", "bytes=0-4095").build()
        client.newCall(request).execute().use { response ->
            val body = if (readBody || isPlaylist(url)) response.body?.string().orEmpty() else ""
            val contentType = response.header("Content-Type").orEmpty()
            val shortUrl = url.substringBefore('?')
            Log.d("AEXT", "probe $shortUrl -> HTTP ${response.code} $contentType (${body.length} bytes)")
            val tracks = if (body.contains("#EXT-X-MEDIA")) hlsTracks(body) else emptyList<String>() to emptyList<String>()
            // Un 200 con una playlist vacía o una página de error es el fallo
            // típico que el reproductor resume como `loading failed`. Lo
            // convertimos en un error concreto y trazable.
            val detail = when {
                !response.isSuccessful -> if (response.code == 403) "HTTP 403 (token/región rechazados)" else "HTTP ${response.code}"
                contentType.startsWith("text/html", ignoreCase = true) -> "HTML en vez de stream"
                body.contains("session blocked", ignoreCase = true)
                    || body.contains("bot session", ignoreCase = true) -> "sesión bloqueada por el sitio"
                isPlaylist(url) && !body.contains("#EXTM3U") -> "playlist no válida"
                isPlaylist(url) && !body.contains("#EXT-X-STREAM-INF") && !body.contains("#EXTINF") ->
                    "playlist vacía (${body.length} bytes)"
                else -> ""
            }
            if (detail.isNotEmpty()) {
                Log.w("AEXT", "probe fallida $shortUrl: $detail")
                StreamProbe(false, detail, body.length, body)
            } else {
                StreamProbe(
                    true, "HTTP ${response.code}", body.length, body,
                    audioLangs = tracks.first, subLangs = tracks.second,
                )
            }
        }
    } catch (e: Exception) {
        // Un fallo de red no prueba que el stream esté roto: conservamos el
        // candidato y dejamos constancia de que no se pudo verificar.
        Log.w("AEXT", "probe de red falló: ${url.substringBefore('?')} -> ${e.message}", e)
        StreamProbe(false, e.message ?: "error de red", inconclusive = true)
    }

    private fun checkStream(url: String, extra: Headers): StreamProbe {
        val first = probeOnce(url, extra, readBody = true)
        if (!first.ok || !isPlaylist(url)) return first
        val childLine = first.body.lineSequence()
            .map { it.trim() }
            .firstOrNull { it.isNotBlank() && !it.startsWith("#") }
            ?: return StreamProbe(false, "máster sin variantes")
        val childUrl = absolutize(url, childLine)
        Log.d("AEXT", "variante ${childUrl.substringBefore('?')}")
        val child = probeOnce(childUrl, extra, readBody = true)
        if (!child.ok && !child.inconclusive) {
            return StreamProbe(false, "variante: ${child.detail}", inconclusive = child.inconclusive)
        }
        if (child.ok && !child.body.contains("#EXTINF") && !child.body.contains("#EXT-X-STREAM-INF")) {
            return StreamProbe(false, "variante vacía (${child.body.length} bytes)")
        }
        val keyRef = hlsKeyRegex.find(child.body)?.groupValues?.get(1)
            ?: hlsKeyRegex.find(first.body)?.groupValues?.get(1)
        if (keyRef != null) {
            val key = probeOnce(absolutize(childUrl, keyRef), extra, readBody = true)
            if (!key.ok && !key.inconclusive) return StreamProbe(false, "clave AES: ${key.detail}")
            if (key.ok && key.size == 0) {
                return StreamProbe(false, "clave AES vacía")
            }
        }
        Log.d("AEXT", "HLS verificado ${url.substringBefore('?')}")
        return StreamProbe(
            true, "HLS verificado", first.size,
            audioLangs = (first.audioLangs + child.audioLangs).distinct(),
            subLangs = (first.subLangs + child.subLangs).distinct(),
        )
    }

    private fun capabilityRank(video: Video): Int {
        val url = video.videoUrl.orEmpty()
        val format = when {
            isPlaylist(url) -> "hls"
            url.contains(".mpd", ignoreCase = true) -> "dash"
            url.contains(".mp4", ignoreCase = true) -> "mp4"
            else -> ""
        }
        return if (format.isNotEmpty() && siteStreamFormats.contains(format)) 0 else 1
    }

    /** Verifica cada candidato; si falla con sus cabeceras, reintenta con
     *  cabeceras de navegador y entrega el Video con las que sí funcionaron.
     *  Si ninguno funciona, lanza un error concreto en vez de dejar que el
     *  reproductor muestre un genérico `loading failed`. */
    /** Reconstruye un Video con el constructor público de 4 argumentos. Evita
     *  `copy`/`copy$default`: esa función sintética no existe en todas las
     *  versiones de la librería de extensiones y provoca un NoSuchMethodError
     *  en tiempo de ejecución antes de mostrar las calidades. */
    private fun rebuildVideo(video: Video, quality: String, headers: Headers?): Video {
        return Video(video.url, quality, video.videoUrl, headers)
    }

    /** Etiqueta con idiomas REALES del HLS. Si no hay pistas declaradas, no añade nada. */
    private fun labelWithTracks(video: Video, probe: StreamProbe): Video {
        val parts = mutableListOf<String>()
        if (probe.audioLangs.isNotEmpty()) parts.add("audio " + probe.audioLangs.joinToString("/"))
        if (probe.subLangs.isNotEmpty()) parts.add("subs " + probe.subLangs.joinToString("/"))
        if (probe.inconclusive) parts.add("sin verificar")
        if (parts.isEmpty()) return video
        val labeled = video.quality.orEmpty() + " · " + parts.joinToString(" · ")
        return rebuildVideo(video, labeled, video.headers)
    }

    private fun verifyCandidates(candidates: List<Video>): List<Video> {
        if (candidates.isEmpty()) return candidates
        val verified = mutableListOf<Video>()
        val failures = mutableListOf<String>()
        for (video in candidates.sortedBy(::capabilityRank)) {
            val url = video.videoUrl ?: continue
            val own = checkStream(url, video.headers ?: headers)
            if (own.ok || own.inconclusive) {
                if (own.inconclusive) {
                    Log.w("AEXT", "candidato entregado sin verificación de red: ${url.substringBefore('?')} (${own.detail})")
                }
                verified.add(labelWithTracks(video, own))
                continue
            }
            val browser = checkStream(url, browserHeaders)
            if (browser.ok || browser.inconclusive) {
                val labeled = labelWithTracks(video, browser)
                val chosen = if (browser.ok) {
                    rebuildVideo(labeled, labeled.quality.orEmpty(), browserHeaders)
                } else {
                    labeled
                }
                if (browser.inconclusive) {
                    Log.w("AEXT", "candidato entregado con cabeceras de navegador sin verificar: ${url.substringBefore('?')}")
                }
                verified.add(chosen)
                continue
            }
            val failure = "${url.substringBefore('?')}: ${own.detail}; con cabeceras de navegador: ${browser.detail}"
            failures.add(failure)
            Log.w("AEXT", "stream descartado: $failure")
        }
        if (verified.isEmpty()) {
            val message = "AEXT no pudo verificar ningún stream (${failures.take(3).joinToString(" | ")})"
            Log.e("AEXT", message)
            throw Exception(message)
        }
        return distinctVideos(verified)
    }

    // ============================== ESTRATEGIAS DE RESOLUCIÓN ==============================
    // Un sitio puede exponer el stream detrás de una API privada o como
    // embebido en HTML. AEXT no asume una sola ruta: prueba varias en orden y
    // aprende cuál funciona: guarda aciertos/fallos por estrategia y ordena
    // las siguientes reproducciones por probabilidad de éxito (suavizado de
    // Laplace). Así, si el sitio cambia de API, la estrategia que empieza a
    // fallar baja de puesto automáticamente sin regenerar la extensión.
    private data class VideoContext(val url: String, val html: String)

    private class VideoStrategy(
        val id: String,
        val priority: Int,
        val resolve: (VideoContext) -> List<Video>?,
    )

    private fun strategyStats(id: String): Pair<Int, Int> {
        val raw = preferences.getString("aext_strategy_$id", "0,0") ?: "0,0"
        val parts = raw.split(",")
        return (parts.getOrNull(0)?.toIntOrNull() ?: 0) to (parts.getOrNull(1)?.toIntOrNull() ?: 0)
    }

    private fun recordStrategy(id: String, success: Boolean) {
        val (ok, fail) = strategyStats(id)
        preferences.edit()
            .putString("aext_strategy_$id", if (success) "${ok + 1},$fail" else "$ok,${fail + 1}")
            .apply()
    }

    /**
     * Orden inicial de los resolutores. AEXT no decide por dominio: el
     * catálogo real que devuelva cada motor decide después, con aciertos y
     * fallos contados en preferencias.
     */
    private fun strategyPriority(id: String): Int = when (id) {
        "html-embeds" -> 0
        else -> 1
    }

    private fun orderedStrategies(): List<VideoStrategy> {
        val all = listOf(
            VideoStrategy("html-embeds", strategyPriority("html-embeds")) { ctx -> htmlEmbedVideos(ctx) },
        )
        return all.sortedWith(
            compareByDescending<VideoStrategy> { s ->
                val (ok, fail) = strategyStats(s.id)
                (ok + 1.0) / (ok + fail + 2.0)
            }.thenBy { it.priority }
        )
    }

    /**
     * Mapa url -> audio ("SUB"/"DUB") leyendo los bloques `SUB:[{server:...,url:...}]`
     * del payload. Sin esto, un iframe del reproductor por defecto puede etiquetarse
     * como DUB solo porque la pestaña DUB del DOM aparece mas cerca en el HTML.
     */
    private fun audioBlocks(html: String): Map<String, String> {
        val map = mutableMapOf<String, String>()
        val pairRegex = Regex("""server\s*:\\?\s*["']([^"']+)["']\s*,\s*url\s*:\s*\\?\s*["'](https?://[^"']+)["']""")
        val labels = listOf("SUB" to "SUB", "DUB" to "DUB", "LAT" to "DUB", "CAST" to "DUB")
        for ((label, audio) in labels) {
            val blockRegex = Regex("""$label\s*:\s*\[([^\]]*)]""")
            for (block in blockRegex.findAll(html)) {
                for (match in pairRegex.findAll(block.groupValues[1])) {
                    map[match.groupValues[2].substringBefore("?")] = audio
                }
            }
        }
        return map
    }

    override fun videoListParse(response: Response): List<Video> {
        val finalUrl = response.request.url.toString()
        // El cuerpo se lee una sola vez: el HTML se reutiliza en todas las
        // estrategias y `asJsoup()` también lo consumiría.
        val html = response.body?.string().orEmpty()
        val ctx = VideoContext(finalUrl, html)
        val errors = mutableListOf<String>()
        val strategies = orderedStrategies()
        Log.d("AEXT", "videoListParse $finalUrl · métodos ${strategies.map { it.id }} · HTML ${html.length} bytes")

        for (strategy in strategies) {
            val candidates = try {
                strategy.resolve(ctx)
            } catch (e: Exception) {
                val message = "${strategy.id}: ${e.message ?: "error"}"
                Log.e("AEXT", "resolutor $message", e)
                errors.add(message)
                recordStrategy(strategy.id, false)
                null
            }
            if (candidates == null) {
                Log.d("AEXT", "${strategy.id}: no aplicable a esta página")
                continue // estrategia no aplicable a este sitio
            }
            if (candidates.isEmpty()) {
                recordStrategy(strategy.id, false)
                errors.add("${strategy.id}: sin candidatos")
                Log.e("AEXT", "${strategy.id}: sin candidatos")
                continue
            }
            val verified = try {
                verifyCandidates(candidates)
            } catch (e: Exception) {
                val message = "${strategy.id}: ${e.message ?: "sin stream válido"}"
                Log.e("AEXT", "verificación $message", e)
                errors.add(message)
                recordStrategy(strategy.id, false)
                continue
            }
            recordStrategy(strategy.id, true)
            Log.d("AEXT", "${strategy.id}: ${verified.size} streams verificados")
            return verified
        }
        val detail = if (errors.isEmpty()) "ningún método aplicable a esta página" else errors.take(4).joinToString(" | ")
        val message = "AEXT no pudo resolver ningún stream ($detail)"
        Log.e("AEXT", message)
        throw Exception(message)
    }

    private fun htmlEmbedVideos(ctx: VideoContext): List<Video> {
        val html = ctx.html
        val document = Jsoup.parse(html, ctx.url)
        val videos = mutableListOf<Video>()
        val blacklist = preferences.getStringSet("server_blacklist", emptySet()) ?: emptySet()

        val embeds = linkedSetOf<Pair<String, String>>()

        document.select("iframe[src], iframe[data-src]").forEach { el ->
            val src = el.attr("abs:src").ifBlank { el.attr("abs:data-src") }
            if (src.startsWith("http")) embeds.add(el.attr("data-server").ifBlank { "" } to src)
        }

        document.select("video[src], video source[src]").forEach { el ->
            val src = el.attr("abs:src")
            if (src.startsWith("http")) embeds.add("" to src)
        }

        val dataAttrs = listOf("data-video", "data-url", "data-embed", "data-src")
        document.select("[data-video], [data-url], [data-embed]").forEach { el ->
            val src = dataAttrs.firstNotNullOfOrNull { name ->
                el.attr(name).takeIf { it.isNotBlank() }
            } ?: return@forEach
            val absolute = absolutize(ctx.url, src)
            if (absolute.startsWith("http")) {
                embeds.add(el.attr("data-server").ifBlank { el.text().trim() } to absolute)
            }
        }

        val serverPatterns = listOf(
            Regex("""server\s*:\s*["']([^"']+)["']\s*,\s*url\s*:\s*["'](https?://[^"']+)["']"""),
            Regex("""["']server["']\s*:\s*["']([^"']+)["']\s*,\s*["']url["']\s*:\s*["'](https?://[^"']+)["']"""),
            Regex("""(?:server|host|name)\s*:\s*["']([^"']+)["'][^{}]{0,160}?(?:url|file|embed|link)\s*:\s*["'](https?://[^"']+)["']"""),
            Regex("""["'](?:server|host|name)["']\s*:\s*["']([^"']+)["'][^{}]{0,160}?["'](?:url|file|embed|link)["']\s*:\s*["'](https?://[^"']+)["']""")
        )
        for (pattern in serverPatterns) {
            for (match in pattern.findAll(html)) {
                embeds.add(match.groupValues[1] to match.groupValues[2])
            }
        }

        // Playlists HLS embebidas directamente en el HTML (payload tipo SvelteKit / JSON).
        val rawPlaylists = Regex("""(https?://[^"'\s\\]+\.m3u8[^"'\s\\]*)""")
        for (match in rawPlaylists.findAll(html)) {
            embeds.add("" to match.groupValues[1].replace("\\/", "/"))
        }

        val seen = mutableSetOf<String>()
        val audioMap = audioBlocks(html)
        for ((declared, embedUrl) in embeds) {
            val key = embedUrl.substringBefore("?").lowercase()
            if (!seen.add(key)) continue

            val server = declared.ifBlank { serverFromUrl(embedUrl) }
            if (isBlacklisted(blacklist, server, embedUrl)) continue

            val audio = audioMap[embedUrl.substringBefore("?")] ?: detectAudio(html, embedUrl)
            runCatching {
                videos.addAll(resolveVideos(server, embedUrl, audio, headers))
            }
        }

        return sortVideos(videos)
    }
    private fun serverFromUrl(url: String): String {
        val host = url.substringAfter("://", url).substringBefore("/").lowercase()
        return when {
            host.contains("mp4upload") -> "MP4Upload"
            host.contains("voe") -> "Voe"
            host.contains("streamwish") || host.contains("byse") || host.contains("swhoi") -> "Byse"
            host.contains("zilla") -> "Zilla"
            host.contains("mega") -> "Mega"
            host.contains("upnshare") -> "UPNShare"
            host.contains("filemoon") -> "Filemoon"
            host.contains("dood") -> "Dood"
            else -> "Direct"
        }
    }

    private fun isBlacklisted(blacklist: Set<String>, server: String, url: String): Boolean {
        if (blacklist.isEmpty()) return false
        val s = server.lowercase()
        val u = url.lowercase()
        return blacklist.any { raw ->
            val key = raw.lowercase()
            key.isNotEmpty() && (
                s.contains(key) || u.contains(key) ||
                    (key == "zilla" && (s.contains("hls") || u.contains("zilla"))) ||
                    (key == "byse" && (s.contains("streamwish") || u.contains("streamwish"))) ||
                    (key == "direct" && s.contains("direct"))
                )
        }
    }

    /** Sin evidencia explícita NO se etiqueta nada: mejor silencio que un DUB falso. */
    private fun detectAudio(html: String, url: String): String {
        val idx = html.indexOf(url)
        if (idx <= 0) return ""
        val before = html.substring(0, idx).uppercase()
        val dubAt = maxOf(
            before.lastIndexOf("DUB"),
            before.lastIndexOf("LAT"),
            before.lastIndexOf("CAST"),
            before.lastIndexOf("AUDIO LATINO")
        )
        val subAt = before.lastIndexOf("SUB")
        return when {
            dubAt >= 0 && dubAt > subAt -> "DUB"
            subAt >= 0 -> "SUB"
            else -> ""
        }
    }

    private fun resolveVideos(server: String, url: String, audio: String, baseHeaders: Headers): List<Video> {
        val prefix = if (audio.isBlank()) "" else "[$audio] "
        val s = server.lowercase()
        val u = url.lowercase()
        return when {
            s.contains("mp4upload") || u.contains("mp4upload") ->
                mp4uploadVideos(url, prefix + "MP4Upload - ", baseHeaders)
            s.contains("voe") || u.contains("voe.sx") ->
                voeVideos(url, prefix + "Voe - ", baseHeaders)
            s.contains("byse") || s.contains("streamwish") || u.contains("streamwish") || u.contains("byse") ->
                streamWishVideos(url, prefix + "Byse - ", baseHeaders)
            s.contains("zilla") || u.contains("zilla-networks") ->
                zillaVideos(url, prefix + "Zilla - ")
            u.contains("mega.nz") || u.contains("mega.co.nz") || s.contains("mega") -> emptyList()
            u.contains(".m3u8") -> hlsVideos(url, prefix, baseHeaders)
            u.contains(".mp4") -> listOf(Video(url, prefix + "MP4", url, baseHeaders))
            else -> genericVideos(url, prefix + server + " - ", baseHeaders)
        }
    }

    private fun embedHeaders(url: String, origin: String, baseHeaders: Headers): Headers =
        baseHeaders.newBuilder()
            .set("Referer", url)
            .set("Origin", origin)
            .set("Accept", "*/*")
            .set("Accept-Language", "es-ES,es;q=0.9,en;q=0.8")
            .set("Sec-Fetch-Dest", "empty")
            .set("Sec-Fetch-Mode", "cors")
            .set("Sec-Fetch-Site", "cross-site")
            .build()

    private fun fetchText(url: String, extra: Headers): String = runCatching {
        val request = Request.Builder().url(url).headers(extra).build()
        client.newCall(request).execute().use { response -> response.body?.string().orEmpty() }
    }.getOrDefault("")

    private fun absolutize(base: String, ref: String): String {
        val clean = ref.trim()
        if (clean.startsWith("http://") || clean.startsWith("https://")) return clean
        if (clean.startsWith("//")) return "https:" + clean
        val origin = originOf(base)
        if (clean.startsWith("/")) return origin + clean
        return base.substringBeforeLast('/', base) + "/" + clean
    }

    private fun originOf(url: String): String {
        val schemeEnd = url.indexOf("://")
        if (schemeEnd < 0) return ""
        val hostEnd = url.indexOf('/', schemeEnd + 3)
        return if (hostEnd < 0) url else url.substring(0, hostEnd)
    }

    private fun isMediaUrl(url: String): Boolean =
        url.contains(".mp4") || url.contains(".m3u8")

    private fun qualityFromUrlOnly(url: String): String =
        normalizeQuality(resolutionRegex.find(url)?.groupValues?.get(1)) ?: "Auto"

    private fun qualityFromBody(url: String, body: String): String {
        normalizeQuality(resolutionRegex.find(url)?.groupValues?.get(1))?.let { return it }
        normalizeQuality(resolutionRegex.find(body)?.groupValues?.get(1))?.let { return it }
        return "Auto"
    }

    private fun distinctVideos(videos: List<Video>): List<Video> {
        val seen = mutableSetOf<String>()
        // `videoUrl` es nullable en extensions-lib: sin `?` esto no compila.
        return videos.filter { seen.add(it.videoUrl?.substringBefore("?") ?: "") }
    }

    private fun mp4uploadVideos(url: String, prefix: String, baseHeaders: Headers): List<Video> {
        val videoHeaders = embedHeaders(url, "https://www.mp4upload.com", baseHeaders)
        val body = fetchText(url, videoHeaders)
        if (body.isBlank()) return emptyList()

        val srcRegex = Regex("""["']?(?:src|file|stream)["']?\s*[:=]\s*["']([^"']+)["']""")
        val labelRegex = Regex("""["']?(?:label|quality|res|resolution|size)["']?\s*[:=]\s*["']?(\d{3,4})p?(?![a-zA-Z])""")
        val objectRegex = Regex("""\{[^{}]*\}""")

        val videos = mutableListOf<Video>()
        for (obj in objectRegex.findAll(body)) {
            val chunk = obj.value
            val raw = srcRegex.find(chunk)?.groupValues?.get(1) ?: continue
            if (!isMediaUrl(raw)) continue
            val absolute = absolutize(url, raw)
            val label = normalizeQuality(labelRegex.find(chunk)?.groupValues?.get(1))
                ?: qualityFromBody(absolute, chunk)
            videos.add(Video(absolute, prefix + label, absolute, videoHeaders))
        }

        if (videos.isEmpty()) {
            val raw = srcRegex.findAll(body).map { it.groupValues[1] }.firstOrNull { isMediaUrl(it) }
            if (raw != null) {
                val absolute = absolutize(url, raw)
                videos.add(Video(absolute, prefix + qualityFromBody(absolute, body), absolute, videoHeaders))
            }
        }

        return distinctVideos(videos)
    }

    private fun isDecoyUrl(url: String): Boolean =
        url.contains("test-videos.co.uk") || url.contains("bigbuckbunny")

    private fun voeVideos(url: String, prefix: String, baseHeaders: Headers): List<Video> {
        var pageUrl = url
        var voeHeaders = embedHeaders(pageUrl, "https://voe.sx", baseHeaders)
        var body = fetchText(pageUrl, voeHeaders)
        if (body.isBlank()) return emptyList()

        // Voe rota de dominio con un redirect por JS: window.location.href = "https://<mirror>/e/<id>".
        if (!body.contains("\"hls\"") && !body.contains(".m3u8")) {
            val redirect = Regex("""window\.location\.href\s*=\s*['"](https?://[^'"]+)['"]""")
                .find(body)?.groupValues?.get(1)
            if (redirect != null && redirect != pageUrl) {
                pageUrl = redirect
                voeHeaders = embedHeaders(pageUrl, "https://voe.sx", baseHeaders)
                body = fetchText(pageUrl, voeHeaders)
            }
        }

        val hls = Regex("""["']hls["']\s*:\s*["']([^"']+)["']""").find(body)?.groupValues?.get(1)
            ?: Regex("""["'](?:file|source|stream|video_url|direct_access_url)["']\s*:\s*["']([^"']+m3u8[^"']*)["']""").find(body)?.groupValues?.get(1)
        val mp4 = Regex("""["']mp4["']\s*:\s*["']([^"']+)["']""").find(body)?.groupValues?.get(1)
            ?: Regex("""var\s+source\s*=\s*['"]([^'"]+)['"]""").find(body)?.groupValues?.get(1)

        val videos = mutableListOf<Video>()
        if (hls != null) videos.addAll(hlsVideos(absolutize(pageUrl, hls), prefix, baseHeaders, voeHeaders))
        if (mp4 != null) {
            val absolute = absolutize(pageUrl, mp4)
            if (!isDecoyUrl(absolute)) {
                videos.add(Video(absolute, prefix + qualityFromBody(absolute, body), absolute, voeHeaders))
            }
        }
        return distinctVideos(videos)
    }

    private fun streamWishVideos(url: String, prefix: String, baseHeaders: Headers): List<Video> {
        val hdrs = embedHeaders(url, originOf(url), baseHeaders)
        val body = fetchText(url, hdrs)
        if (body.isBlank()) return emptyList()

        val raw = Regex("""["']?(?:file|source|hls)["']?\s*[:=]\s*["']([^"']+(?:m3u8|mp4)[^"']*)["']""")
            .find(body)?.groupValues?.get(1) ?: return emptyList()
        val absolute = absolutize(url, raw)
        return if (absolute.contains(".m3u8")) {
            hlsVideos(absolute, prefix, baseHeaders, hdrs)
        } else {
            listOf(Video(absolute, prefix + qualityFromBody(absolute, body), absolute, hdrs))
        }
    }

    private fun zillaVideos(url: String, prefix: String): List<Video> {
        val hash = url.substringAfterLast("/play/").substringAfterLast("/m3u8/")
            .substringBefore("?").substringBefore("/").trim()
        if (hash.length < 16) return emptyList()
        val playlist = "https://player.zilla-networks.com/m3u8/$hash"

        // Cabeceras exactas que Cloudflare exige en Zilla: sin Origin/Referer
        // correctos devuelve 403 o corta el segmento y el player salta al final.
        val zillaHeaders = Headers.Builder()
            .set("User-Agent", userAgent)
            .set("Referer", "https://player.zilla-networks.com/play/$hash")
            .set("Origin", "https://player.zilla-networks.com")
            .set("Accept", "*/*")
            .set("Accept-Language", "es-ES,es;q=0.9,en;q=0.8")
            .set("Sec-Fetch-Dest", "empty")
            .set("Sec-Fetch-Mode", "cors")
            .set("Sec-Fetch-Site", "same-origin")
            .build()

        // Warm-up: deja que cloudflareClient resuelva un posible reto y guarde la
        // cookie cf_clearance en el mismo jar que usan playlist y segmentos.
        fetchText("https://player.zilla-networks.com/play/$hash", zillaHeaders)

        return hlsVideos(playlist, prefix, zillaHeaders, zillaHeaders)
    }

    private fun genericVideos(url: String, prefix: String, baseHeaders: Headers): List<Video> {
        var pageUrl = url
        var hdrs = embedHeaders(pageUrl, originOf(pageUrl), baseHeaders)
        var body = fetchText(pageUrl, hdrs)

        val mediaRegex = Regex("""["']?(?:file|source|src|hls|playlist)["']?\s*[:=]\s*["']([^"']+(?:\.m3u8|\.mp4)[^"']*)["']""")

        // Hosts que rotan de dominio con un redirect por JS.
        if (!mediaRegex.containsMatchIn(body)) {
            val redirect = Regex("""window\.location\.href\s*=\s*['"](https?://[^'"]+)['"]""")
                .find(body)?.groupValues?.get(1)
            if (redirect != null && redirect != pageUrl) {
                pageUrl = redirect
                hdrs = embedHeaders(pageUrl, originOf(pageUrl), baseHeaders)
                body = fetchText(pageUrl, hdrs)
            }
        }
        if (body.isBlank()) return emptyList()

        val raw = mediaRegex.find(body)?.groupValues?.get(1)
            ?: Regex("""(https?://[^"'\s\\]+\.m3u8[^"'\s\\]*)""").find(body)?.groupValues?.get(1)
            ?: Regex("""(https?://[^"'\s\\]+\.mp4[^"'\s\\]*)""").find(body)?.groupValues?.get(1)
            ?: return emptyList()

        val absolute = absolutize(pageUrl, raw.replace("\\/", "/"))
        return if (absolute.contains(".m3u8")) {
            hlsVideos(absolute, prefix, baseHeaders, hdrs)
        } else {
            listOf(Video(absolute, prefix + qualityFromBody(absolute, body), absolute, hdrs))
        }
    }

    /**
     * Expande un master HLS (`#EXT-X-STREAM-INF`) en una entrada `Video` por
     * variante, con su resolución y bitrate reales. Asi el reproductor muestra
     * TODAS las calidades del m3u8 en vez de una sola entrada opaca.
     */
    private fun hlsVideos(url: String, prefix: String, baseHeaders: Headers, extra: Headers = baseHeaders): List<Video> {
        val body = fetchText(url, extra)
        if (body.isBlank() || !body.contains("#EXTM3U")) {
            return listOf(Video(url, prefix + "HLS", url, extra))
        }
        if (!body.contains("#EXT-X-STREAM-INF")) {
            return listOf(Video(url, prefix + qualityFromBody(url, body), url, extra))
        }

        val videos = mutableListOf<Video>()
        var height: String? = null
        var bandwidth: Long? = null
        for (rawLine in body.lines()) {
            val line = rawLine.trim()
            if (line.startsWith("#EXT-X-STREAM-INF")) {
                height = normalizeQuality(Regex("""RESOLUTION=\d+x(\d{3,4})""").find(line)?.groupValues?.get(1))
                bandwidth = Regex("""BANDWIDTH=(\d+)""").find(line)?.groupValues?.get(1)?.toLongOrNull()
            } else if (line.isNotEmpty() && !line.startsWith("#")) {
                val currentHeight = height ?: continue
                val currentBandwidth = bandwidth
                val variant = absolutize(url, line)
                val bitrate = if (currentBandwidth != null && currentBandwidth > 0) {
                    " (" + (currentBandwidth / 1000) + " kbps)"
                } else {
                    ""
                }
                videos.add(Video(variant, prefix + currentHeight + bitrate, variant, extra))
                height = null
                bandwidth = null
            }
        }

        return videos.ifEmpty { listOf(Video(url, prefix + "HLS", url, extra)) }
    }

    private fun sortVideos(videos: List<Video>): List<Video> {
        val prefAudio = preferences.getString("preferred_audio", "all") ?: "all"
        val prefServer = preferences.getString("preferred_server", "any") ?: "any"
        val prefQuality = preferences.getString("preferred_quality", "1080p") ?: "1080p"
        return videos.sortedWith(
            compareBy<Video>(
                { audioRank(it.quality, prefAudio) },
                { serverRank(it.quality, prefServer) },
                { qualityRank(it.quality, prefQuality) },
                { -qualityHeight(it.quality) }
            )
        )
    }

    /** 0 = calidad preferida, 1 = desconocida, 2 = mayor (downscale), 3 = menor (upscale). */
    private fun qualityRank(quality: String, pref: String): Int {
        val target = pref.removeSuffix("p").toIntOrNull() ?: return 0
        val height = qualityHeight(quality)
        if (height == 0) return 1
        if (height == target) return 0
        return if (height > target) 2 else 3
    }

    private fun audioRank(quality: String, pref: String): Int = when (pref) {
        "sub" -> if (quality.contains("[SUB]")) 0 else 1
        "dub" -> if (quality.contains("[DUB]")) 0 else 1
        else -> 0
    }

    private fun serverRank(quality: String, pref: String): Int {
        val needle = when (pref) {
            "mp4upload" -> "MP4Upload"
            "voe" -> "Voe"
            "byse" -> "Byse"
            "zilla" -> "Zilla"
            "direct" -> "Direct"
            else -> return 0
        }
        return if (quality.contains(needle, ignoreCase = true)) 0 else 1
    }

    private fun qualityHeight(quality: String): Int {
        val raw = resolutionRegex.find(quality)?.groupValues?.get(1)?.toIntOrNull() ?: return 0
        return if (raw in 144..4320) raw else 0
    }


    // ============================== PREFERENCES ==============================
    override fun setupPreferenceScreen(screen: PreferenceScreen) {
        val methodsPref = ListPreference(screen.context).apply {
            key = "aext_methods_found"
            title = "Métodos disponibles encontrados de reproducción"
            entries = arrayOf("Reproductor incrustado", "MP4 directo")
            entryValues = arrayOf("Reproductor incrustado", "MP4 directo")
            summary = "Reproductor incrustado, MP4 directo"
            setDefaultValue(arrayOf("Reproductor incrustado", "MP4 directo").first())
        }
        screen.addPreference(methodsPref)

        val serverPref = ListPreference(screen.context).apply {
            key = "preferred_server"
            title = "Servidor Preferido (Stream por Defecto)"
            entries = arrayOf("Any", "MP4Upload", "Voe", "Byse (StreamWish)", "Zilla (HLS)", "Direct")
            entryValues = arrayOf("any", "mp4upload", "voe", "byse", "zilla", "direct")
            setDefaultValue("any")
            summary = "Solo reordena servidores ya detectados; no activa un servidor que no está disponible. — %s"
        }
        screen.addPreference(serverPref)

        val qualityPref = ListPreference(screen.context).apply {
            key = "preferred_quality"
            title = "Calidad Preferida (Stream por Defecto)"
            entries = arrayOf("1080p", "720p", "480p", "360p")
            entryValues = arrayOf("1080p", "720p", "480p", "360p")
            setDefaultValue("1080p")
            summary = "%s"
        }
        screen.addPreference(qualityPref)

        val serverBlacklistPref = MultiSelectListPreference(screen.context).apply {
            key = "server_blacklist"
            title = "Lista Negra de Servidores (Ocultar)"
            entries = arrayOf("MP4Upload", "Voe", "Byse (StreamWish)", "Zilla (HLS)", "Directos")
            entryValues = arrayOf("mp4upload", "voe", "byse", "zilla", "direct")
            setDefaultValue(emptySet<String>())
            summary = "Desactiva servidores en el reproductor (ej. Zilla)"
        }
        screen.addPreference(serverBlacklistPref)

        val genreBlacklistPref = EditTextPreference(screen.context).apply {
            key = "genre_blacklist_text"
            title = "Lista Negra de Géneros (# - Ocultar)"
            setDefaultValue("")
            summary = "Oculta permanentemente animes con estos géneros (#) en el catálogo"
            dialogTitle = "Lista Negra de Géneros (# - Ocultar)"
        }
        screen.addPreference(genreBlacklistPref)
    }
}
