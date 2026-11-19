package eu.kanade.tachiyomi.animeextension.all.frame

import eu.kanade.tachiyomi.animesource.model.AnimeFilter
import eu.kanade.tachiyomi.animesource.model.AnimeFilterList

object FrameFilters {
    class Header(name: String) : AnimeFilter.Header(name)

    class ParamSelectFilter(name: String, val param: String, val vals: Array<Pair<String, String>>) :
        AnimeFilter.Select<String>(name, (listOf("All") + vals.map { it.first }).toTypedArray())

    class ParamCheckFilter(name: String, val param: String, val value: String) :
        AnimeFilter.CheckBox(name, false)

    class ParamTextFilter(name: String, val param: String) : AnimeFilter.Text(name)

    fun getFilterList(): AnimeFilterList = AnimeFilterList(
        Header("Search & Catalog Filters"),
        ParamCheckFilter("Dubbed", "isFrameDub", "true"),
        ParamTextFilter("Appdeeplinkscheme", "appDeeplinkScheme"),
        ParamTextFilter("Appstorelink", "appstoreLink"),
        ParamTextFilter("Authenticationcodecooldown", "authenticationCodeCooldown"),
        ParamTextFilter("Authenticationqrcooldown", "authenticationQrCooldown"),
        ParamTextFilter("Cookiesexpireindays", "cookiesExpireInDays"),
        ParamTextFilter("Googleplaylink", "googleplayLink"),
        ParamTextFilter("Gtmevents", "gtmEvents"),
        ParamTextFilter("Huaweistorelink", "huaweiStoreLink"),
        ParamTextFilter("Modals", "modals"),
        ParamTextFilter("Siteurl", "siteUrl"),
        ParamTextFilter("Clickmerchant", "clickMerchantId"),
        ParamTextFilter("Clickservice", "clickServiceId"),
        ParamTextFilter("Parsecardtype", "parseCardType"),
        ParamTextFilter("Paymemerchant", "paymeMerchantId"),
        ParamTextFilter("Plummerchant", "plumMerchantId"),
        ParamTextFilter("Rendercarderror", "renderCardError"),
        ParamTextFilter("Resolvesubfeatureicon", "resolveSubFeatureIcon"),
    )
}
