package eu.kanade.tachiyomi.animeextension.all.pluto3

import eu.kanade.tachiyomi.animesource.model.AnimeFilter
import eu.kanade.tachiyomi.animesource.model.AnimeFilterList

object Pluto3Filters {
    class Header(name: String) : AnimeFilter.Header(name)

    class ParamSelectFilter(name: String, val param: String, val vals: Array<Pair<String, String>>) :
        AnimeFilter.Select<String>(name, (listOf("All") + vals.map { it.first }).toTypedArray())

    class ParamCheckFilter(name: String, val param: String, val value: String) :
        AnimeFilter.CheckBox(name, false)

    class ParamTextFilter(name: String, val param: String) : AnimeFilter.Text(name)

    fun getFilterList(): AnimeFilterList = AnimeFilterList(
        Header("Search & Catalog Filters"),
        ParamTextFilter("Userreg", "userRegId"),
        ParamTextFilter("Usertype", "userType"),
        ParamTextFilter("Userstatus", "userStatus"),
        ParamTextFilter("Userprofile", "userProfileId"),
        ParamTextFilter("Userprofileref", "userProfileRefId"),
        ParamTextFilter("Userprofilecategory", "userProfileCategory"),
        ParamTextFilter("Userprofilemaster", "userProfileMaster"),
        ParamTextFilter("Packagesource", "packageSource"),
        ParamTextFilter("Packagestatus", "packageStatus"),
        ParamTextFilter("Password", "password"),
        ParamTextFilter("Passwd", "passwd"),
        ParamTextFilter("Pwd", "pwd"),
        ParamTextFilter("Token", "token"),
        ParamTextFilter("Secret", "secret"),
        ParamTextFilter("Authorization", "authorization"),
        ParamTextFilter("Auth", "auth"),
        ParamTextFilter("Bearer", "bearer"),
        ParamTextFilter("Session", "session"),
        ParamTextFilter("Session", "sessionid"),
        ParamTextFilter("S", "sid"),
        ParamTextFilter("Jwt", "jwt"),
        ParamTextFilter("Otp", "otp"),
        ParamTextFilter("Email", "email"),
        ParamTextFilter("Emailaddress", "emailaddress"),
        ParamTextFilter("Referer", "referer"),
        ParamTextFilter("Referrer", "referrer"),
        ParamTextFilter("Credential", "credential"),
        ParamTextFilter("Credentials", "credentials"),
        ParamTextFilter("Cookie", "cookie"),
        ParamTextFilter("Apikey", "apikey"),
        ParamTextFilter("Api Key", "api_key"),
        ParamTextFilter("Access Token", "access_token"),
        ParamTextFilter("Accesstoken", "accesstoken"),
        ParamTextFilter("Refresh Token", "refresh_token"),
        ParamTextFilter("Refreshtoken", "refreshtoken"),
        ParamTextFilter("Id Token", "id_token"),
        ParamTextFilter("Idtoken", "idtoken"),
        ParamTextFilter("Session", "session_id"),
        ParamTextFilter("Adsession", "adsessionid"),
        ParamTextFilter("Code", "code"),
        ParamTextFilter("Signature", "signature"),
        ParamTextFilter("Sig", "sig"),
        ParamTextFilter("User", "userid"),
        ParamTextFilter("Ipaddress", "ipaddress"),
    )
}
