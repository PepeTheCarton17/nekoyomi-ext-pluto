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
        ParamTextFilter("Default Date Format", "DEFAULT_DATE_FORMAT"),
        ParamTextFilter("Default Date Format Without Year", "DEFAULT_DATE_FORMAT_WITHOUT_YEAR"),
        ParamTextFilter("Default Full Date Format", "DEFAULT_FULL_DATE_FORMAT"),
        ParamTextFilter("Default Short Date Time Format", "DEFAULT_SHORT_DATE_TIME_FORMAT"),
        ParamTextFilter("Installment Only Frame Tv", "INSTALLMENT_ONLY_FRAME_TV_IDS"),
        ParamTextFilter("Ls Access", "LS_ACCESS"),
        ParamTextFilter("Ls Identifier", "LS_IDENTIFIER"),
        ParamTextFilter("Ls Notifications Disabled", "LS_NOTIFICATIONS_DISABLED"),
        ParamTextFilter("Ls Selected Content Lang", "LS_SELECTED_CONTENT_LANG"),
        ParamTextFilter("Ls Selected Lang", "LS_SELECTED_LANG"),
        ParamTextFilter("Ls Selected Profile", "LS_SELECTED_PROFILE_ID"),
        ParamTextFilter("Temp Draw Sub Amount", "TEMP_DRAW_SUB_AMOUNT"),
        ParamTextFilter("Apiversion", "apiVersion"),
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
