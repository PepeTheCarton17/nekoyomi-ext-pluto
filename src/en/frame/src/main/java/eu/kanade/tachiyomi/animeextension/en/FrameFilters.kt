package eu.kanade.tachiyomi.animeextension.en.frame

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

    open class PathFilter(displayName: String, val vals: Array<Pair<String, String>>) :
        AnimeFilter.Select<String>(displayName, vals.map { it.first }.toTypedArray()) {
        fun toPath(): String = vals[state].second
    }

    class ExcludeFilter(name: String, val value: String) : AnimeFilter.CheckBox(name, false)

    fun getFilterList(): AnimeFilterList = AnimeFilterList(
        Header("Filtros de búsqueda y catálogo"),
        QueryPartFilter("Estado", "Status", arrayOf(Pair("Todos", ""), Pair("Announcement", "announcement"))),
        QueryPartFilter("Tipo", "Type", arrayOf(Pair("Todos", ""), Pair("Android Tv Source", "android_tv_source"), Pair("Android Source", "android_source"))),
        CheckBoxFilterList("Categoría", "categories", arrayOf(Pair("All", "1"), Pair("Movies", "2"), Pair("Series", "3"), Pair("Cartoons", "4"), Pair("Cartoon series", "5"), Pair("Anime", "6"), Pair("TV show", "7"), Pair("Mini-series", "8"))),
        CheckBoxFilterList("País", "countries", arrayOf(Pair("Afghanistan", "16"), Pair("Albania", "4"), Pair("Algeria", "5"), Pair("American Samoa", "7"), Pair("Andorra", "9"), Pair("Angola", "8"), Pair("Antarctica", "10"), Pair("Antigua and Barbuda", "11"), Pair("Argentina", "13"), Pair("Armenia", "14"), Pair("Aruba", "15"), Pair("Australia", "1"), Pair("Austria", "2"), Pair("Azerbaijan", "3"), Pair("Bahamas", "17"), Pair("Bahrain", "20"), Pair("Bangladesh", "18"), Pair("Barbados", "19"), Pair("Belarus", "21"), Pair("Belgium", "23"), Pair("Belize", "22"), Pair("Benin", "24"), Pair("Bermuda", "26"), Pair("Bhutan", "37"), Pair("Bolivia", "29"), Pair("Bosnia", "30"), Pair("Bosnia and Herzegovina", "31"), Pair("Botswana", "32"), Pair("Brazil", "33"), Pair("Brunei Darussalam", "34"), Pair("Bulgaria", "28"), Pair("Burkina Faso", "35"), Pair("Burma", "27"), Pair("Burundi", "36"), Pair("Cambodia", "90"), Pair("Cameroon", "91"), Pair("Canada", "92"), Pair("Cape Verde", "87"), Pair("Cayman Islands", "89"), Pair("Central African Republic", "220"), Pair("Chad", "221"), Pair("Chile", "225"), Pair("China", "97"), Pair("Colombia", "98"), Pair("Comoros", "99"), Pair("Congo", "100"), Pair("Cook Islands", "153"), Pair("Costa Rica", "106"), Pair("Côte d'Ivoire", "107"), Pair("Croatia", "219"), Pair("Cuba", "108"), Pair("Cyprus", "95"), Pair("Czech Republic", "223"), Pair("Czechoslovakia", "224"), Pair("Democratic Republic of the Congo", "101"), Pair("Denmark", "67"), Pair("Djibouti", "68"), Pair("Dominica", "69"), Pair("Dominican Republic", "70"), Pair("East Germany (GDR)", "57"), Pair("Ecuador", "229"), Pair("Egypt", "71"), Pair("El Salvador", "171"), Pair("Equatorial Guinea", "230"), Pair("Eritrea", "231"), Pair("Estonia", "232"), Pair("Eswatini", "239"), Pair("Ethiopia", "233"), Pair("Falkland Islands", "215"), Pair("Faroe Islands", "210"), Pair("Federal Republic of Yugoslavia", "236"), Pair("Federated States of Micronesia", "211"), Pair("Fiji", "212"), Pair("Finland", "214"), Pair("France", "216"), Pair("French Guiana", "217"), Pair("French Polynesia", "218"), Pair("Gabon", "47"), Pair("Gambia", "50"), Pair("Georgia", "65"), Pair("Germany", "56"), Pair("Ghana", "51"), Pair("Gibraltar", "59"), Pair("Greece", "64"), Pair("Greenland", "63"), Pair("Grenada", "62"), Pair("Guadeloupe", "52"), Pair("Guam", "66"), Pair("Guatemala", "53"), Pair("Guinea", "54"), Pair("Guinea-Bissau", "55"), Pair("Guyana", "49"), Pair("Haiti", "48"), Pair("Honduras", "60"), Pair("Hong Kong", "61"), Pair("Hungary", "41"), Pair("Iceland", "83"), Pair("India", "77"), Pair("Indonesia", "78"), Pair("Iran", "81"), Pair("Iraq", "80"), Pair("Ireland", "82"), Pair("Isle of Man", "152"), Pair("Israel", "76"), Pair("Italy", "85"), Pair("Ivory Coast", "25"), Pair("Jamaica", "237"), Pair("Japan", "238"), Pair("Jordan", "79"), Pair("Kazakhstan", "88"), Pair("Kenya", "94"), Pair("Kiribati", "96"), Pair("Korea", "102"), Pair("Kosovo", "105"), Pair("Kuwait", "109"), Pair("Kyrgyzstan", "110"), Pair("Laos", "111"), Pair("Latvia", "112"), Pair("Lebanon", "115"), Pair("Lesotho", "113"), Pair("Liberia", "114"), Pair("Libya", "116"), Pair("Liechtenstein", "118"), Pair("Lithuania", "117"), Pair("Luxembourg", "119"), Pair("Macau", "123"), Pair("Macedonia", "124"), Pair("Madagascar", "122"), Pair("Malawi", "125"), Pair("Malaysia", "126"), Pair("Maldives", "128"), Pair("Mali", "127"), Pair("Malta", "129"), Pair("Marshall Islands", "132"), Pair("Martinique", "131"), Pair("Mauritania", "121"), Pair("Mauritius", "120"), Pair("Mexico", "133"), Pair("Moldova", "135"), Pair("Monaco", "136"), Pair("Mongolia", "137"), Pair("Montenegro", "222"), Pair("Montserrat", "138"), Pair("Morocco", "130"), Pair("Mozambique", "134"), Pair("Myanmar", "139"), Pair("Namibia", "140"), Pair("Nepal", "141"), Pair("Netherlands", "144"), Pair("Netherlands Antilles", "12"), Pair("New Caledonia", "147"), Pair("New Zealand", "146"), Pair("Nicaragua", "145"), Pair("Niger", "142"), Pair("Nigeria", "143"), Pair("North Korea", "103"), Pair("North Macedonia", "176"), Pair("North Vietnam", "46"), Pair("Norway", "148"), Pair("Occupied Palestinian Territory", "150"), Pair("Oman", "151"), Pair("Pakistan", "154"), Pair("Palau", "155"), Pair("Palestine", "156"), Pair("Panama", "157"), Pair("Papua New Guinea", "158"), Pair("Paraguay", "159"), Pair("Peru", "160"), Pair("Philippines", "213"), Pair("Poland", "161"), Pair("Portugal", "162"), Pair("Puerto Rico", "163"), Pair("Qatar", "93"), Pair("Réunion", "164"), Pair("Romania", "168"), Pair("Russia", "166"), Pair("Russian Empire", "165"), Pair("Rwanda", "167"), Pair("Saint Kitts and Nevis", "180"), Pair("Saint Lucia", "181"), Pair("Saint Vincent and the Grenadines", "179"), Pair("Samoa", "172"), Pair("San Marino", "173"), Pair("Saudi Arabia", "174"), Pair("Senegal", "178"), Pair("Serbia", "182"), Pair("Serbia and Montenegro", "183"), Pair("Seychelles", "177"), Pair("Siam", "184"), Pair("Sierra Leone", "193"), Pair("Singapore", "185"), Pair("Slovakia", "187"), Pair("Slovenia", "188"), Pair("Solomon Islands", "189"), Pair("Somalia", "190"), Pair("South Africa", "234"), Pair("South Korea", "104"), Pair("Spain", "84"), Pair("Sri Lanka", "228"), Pair("Sudan", "191"), Pair("Suriname", "192"), Pair("Swaziland", "175"), Pair("Sweden", "227"), Pair("Switzerland", "226"), Pair("Syria", "186"), Pair("Taiwan", "196"), Pair("Tajikistan", "194"), Pair("Tanzania", "197"), Pair("Thailand", "195"), Pair("Timor-Leste", "198"), Pair("Togo", "199"), Pair("Tonga", "200"), Pair("Trinidad and Tobago", "201"), Pair("Tunisia", "203"), Pair("Turkey", "205"), Pair("Turkmenistan", "204"), Pair("Tuvalu", "202"), Pair("Uganda", "206"), Pair("Ukraine", "208"), Pair("United Arab Emirates", "149"), Pair("United Kingdom", "40"), Pair("United States Minor Outlying Islands", "44"), Pair("United States Virgin Islands", "6"), Pair("Uruguay", "209"), Pair("USA", "170"), Pair("USSR", "169"), Pair("Uzbekistan", "207"), Pair("Vanuatu", "38"), Pair("Vatican", "39"), Pair("Venezuela", "42"), Pair("Vietnam", "45"), Pair("Virgin Islands", "43"), Pair("West Germany (FRG)", "58"), Pair("Western Sahara", "74"), Pair("Yemen", "86"), Pair("Yugoslavia", "235"), Pair("Zaire", "72"), Pair("Zambia", "73"), Pair("Zimbabwe", "75"))),
        QueryPartFilter("Año", "years", arrayOf(Pair("Todos", ""), Pair("2026", "2026"), Pair("2025", "2025"), Pair("2024", "2024"), Pair("2023", "2023"), Pair("2022", "2022"), Pair("2021", "2021"), Pair("2020", "2020"), Pair("2019", "2019"), Pair("2018", "2018"), Pair("2017", "2017"), Pair("2016", "2016"), Pair("2015", "2015"), Pair("2014", "2014"), Pair("2013", "2013"), Pair("2012", "2012"), Pair("2011", "2011"), Pair("2010", "2010"), Pair("2009", "2009"), Pair("2008", "2008"), Pair("2007", "2007"), Pair("2006", "2006"), Pair("2005", "2005"), Pair("2004", "2004"), Pair("2003", "2003"), Pair("2002", "2002"), Pair("2001", "2001"), Pair("2000", "2000"), Pair("1999", "1999"), Pair("1998", "1998"), Pair("1997", "1997"), Pair("1996", "1996"), Pair("1995", "1995"), Pair("1994", "1994"), Pair("1993", "1993"), Pair("1992", "1992"), Pair("1991", "1991"), Pair("1990", "1990"), Pair("1989", "1989"), Pair("1988", "1988"), Pair("1987", "1987"), Pair("1986", "1986"), Pair("1985", "1985"), Pair("1984", "1984"), Pair("1983", "1983"), Pair("1982", "1982"), Pair("1981", "1981"), Pair("1980", "1980"), Pair("1979", "1979"), Pair("1978", "1978"), Pair("1977", "1977"), Pair("1976", "1976"), Pair("1975", "1975"), Pair("1974", "1974"), Pair("1973", "1973"), Pair("1972", "1972"), Pair("1971", "1971"), Pair("1970", "1970"), Pair("1969", "1969"), Pair("1968", "1968"), Pair("1967", "1967"), Pair("1966", "1966"), Pair("1965", "1965"), Pair("1964", "1964"), Pair("1963", "1963"), Pair("1962", "1962"), Pair("1961", "1961"), Pair("1960", "1960"), Pair("1959", "1959"), Pair("1958", "1958"), Pair("1957", "1957"), Pair("1956", "1956"), Pair("1955", "1955"), Pair("1954", "1954"), Pair("1953", "1953"), Pair("1952", "1952"), Pair("1951", "1951"), Pair("1950", "1950"), Pair("1949", "1949"), Pair("1948", "1948"), Pair("1947", "1947"), Pair("1946", "1946"), Pair("1945", "1945"), Pair("1944", "1944"), Pair("1943", "1943"), Pair("1942", "1942"), Pair("1941", "1941"), Pair("1940", "1940"), Pair("1939", "1939"), Pair("1938", "1938"), Pair("1937", "1937"), Pair("1936", "1936"), Pair("1935", "1935"), Pair("1934", "1934"), Pair("1933", "1933"), Pair("1932", "1932"), Pair("1931", "1931"), Pair("1930", "1930"), Pair("1929", "1929"), Pair("1928", "1928"), Pair("1927", "1927"), Pair("1926", "1926"), Pair("1925", "1925"), Pair("1924", "1924"), Pair("1923", "1923"), Pair("1922", "1922"), Pair("1921", "1921"), Pair("1920", "1920"), Pair("1919", "1919"), Pair("1918", "1918"), Pair("1917", "1917"), Pair("1916", "1916"), Pair("1915", "1915"), Pair("1914", "1914"), Pair("1913", "1913"), Pair("1912", "1912"), Pair("1911", "1911"), Pair("1910", "1910"), Pair("1909", "1909"), Pair("1908", "1908"), Pair("1907", "1907"), Pair("1906", "1906"), Pair("1905", "1905"), Pair("1904", "1904"), Pair("1903", "1903"), Pair("1902", "1902"), Pair("1901", "1901"), Pair("1900", "1900"), Pair("1899", "1899"), Pair("1898", "1898"), Pair("1897", "1897"), Pair("1896", "1896"), Pair("1895", "1895"), Pair("1894", "1894"), Pair("1893", "1893"), Pair("1892", "1892"), Pair("1891", "1891"), Pair("1890", "1890"), Pair("1889", "1889"), Pair("1888", "1888"), Pair("1887", "1887"), Pair("1886", "1886"), Pair("1885", "1885"), Pair("1884", "1884"), Pair("1883", "1883"), Pair("1882", "1882"), Pair("1881", "1881"), Pair("1880", "1880"), Pair("1879", "1879"), Pair("1878", "1878"), Pair("1877", "1877"), Pair("1876", "1876"), Pair("1875", "1875"), Pair("1874", "1874"))),
        CheckBoxFilterList("Género", "genres", arrayOf(Pair("Action", "1"), Pair("Adventures", "2"), Pair("Anime", "3"), Pair("Comedy", "4"), Pair("Detective", "5"), Pair("Documentary", "6"), Pair("Drama", "7"), Pair("Family", "8"), Pair("Science Fiction", "9"), Pair("Fantasy", "10"), Pair("Children's", "11"), Pair("Crime", "12"), Pair("Melodrama", "13"), Pair("Military", "14"), Pair("Horror", "15"), Pair("Thriller", "16"), Pair("Western", "17"))),
        QueryPartFilter("Calidad", "qualities", arrayOf(Pair("Todos", ""), Pair("Sd", "Sd"), Pair("Hd", "Hd"), Pair("FullHd", "FullHd"), Pair("UltraHd", "UltraHd"))),
        QueryPartFilter("Categoría de TV", "tv-categories", arrayOf(Pair("Todos", ""), Pair("Entertainment", "18"), Pair("Movie", "19"), Pair("Sport", "20"), Pair("Educational", "21"), Pair("News", "22"), Pair("Musical", "23"), Pair("Kids", "24"), Pair("Informational", "25"))),
        QueryPartFilter("Profesión", "professions", arrayOf(Pair("Todos", ""), Pair("Main actor", "1"), Pair("Actor", "2"), Pair("Cameo", "3"), Pair("Composer", "4"), Pair("Design", "5"), Pair("Director", "6"), Pair("Editor", "7"), Pair("Group cameo", "8"), Pair("Operator", "9"), Pair("Producer", "10"), Pair("Sound designer", "11"), Pair("Translator", "12"), Pair("Uncredited", "13"), Pair("Voice director", "14"), Pair("Voice actor", "15"), Pair("Screenwriter", "16"), Pair("Designer", "17"))),
        Header("Lista Negra de Géneros (# - Excluir)"),
        ExcludeFilter("Categoría: All", "1"),
        ExcludeFilter("Categoría: Movies", "2"),
        ExcludeFilter("Categoría: Series", "3"),
        ExcludeFilter("Categoría: Cartoons", "4"),
        ExcludeFilter("Categoría: Cartoon series", "5"),
        ExcludeFilter("Categoría: Anime", "6"),
        ExcludeFilter("Categoría: TV show", "7"),
        ExcludeFilter("Categoría: Mini-series", "8"),
        ExcludeFilter("País: Afghanistan", "16"),
        ExcludeFilter("País: Albania", "4"),
        ExcludeFilter("País: Algeria", "5"),
        ExcludeFilter("País: American Samoa", "7"),
        ExcludeFilter("País: Andorra", "9"),
        ExcludeFilter("País: Angola", "8"),
        ExcludeFilter("País: Antarctica", "10"),
        ExcludeFilter("País: Antigua and Barbuda", "11"),
        ExcludeFilter("País: Argentina", "13"),
        ExcludeFilter("País: Armenia", "14"),
        ExcludeFilter("País: Aruba", "15"),
        ExcludeFilter("País: Australia", "1"),
        ExcludeFilter("País: Austria", "2"),
        ExcludeFilter("País: Azerbaijan", "3"),
        ExcludeFilter("País: Bahamas", "17"),
        ExcludeFilter("País: Bahrain", "20"),
        ExcludeFilter("País: Bangladesh", "18"),
        ExcludeFilter("País: Barbados", "19"),
        ExcludeFilter("País: Belarus", "21"),
        ExcludeFilter("País: Belgium", "23"),
        ExcludeFilter("País: Belize", "22"),
        ExcludeFilter("País: Benin", "24"),
        ExcludeFilter("País: Bermuda", "26"),
        ExcludeFilter("País: Bhutan", "37"),
        ExcludeFilter("País: Bolivia", "29"),
        ExcludeFilter("País: Bosnia", "30"),
        ExcludeFilter("País: Bosnia and Herzegovina", "31"),
        ExcludeFilter("País: Botswana", "32"),
        ExcludeFilter("País: Brazil", "33"),
        ExcludeFilter("País: Brunei Darussalam", "34"),
        ExcludeFilter("País: Bulgaria", "28"),
        ExcludeFilter("País: Burkina Faso", "35"),
        ExcludeFilter("País: Burma", "27"),
        ExcludeFilter("País: Burundi", "36"),
        ExcludeFilter("País: Cambodia", "90"),
        ExcludeFilter("País: Cameroon", "91"),
        ExcludeFilter("País: Canada", "92"),
        ExcludeFilter("País: Cape Verde", "87"),
        ExcludeFilter("País: Cayman Islands", "89"),
        ExcludeFilter("País: Central African Republic", "220"),
        ExcludeFilter("País: Chad", "221"),
        ExcludeFilter("País: Chile", "225"),
        ExcludeFilter("País: China", "97"),
        ExcludeFilter("País: Colombia", "98"),
        ExcludeFilter("País: Comoros", "99"),
        ExcludeFilter("País: Congo", "100"),
        ExcludeFilter("País: Cook Islands", "153"),
        ExcludeFilter("País: Costa Rica", "106"),
        ExcludeFilter("País: Côte d'Ivoire", "107"),
        ExcludeFilter("País: Croatia", "219"),
        ExcludeFilter("País: Cuba", "108"),
        ExcludeFilter("País: Cyprus", "95"),
        ExcludeFilter("País: Czech Republic", "223"),
        ExcludeFilter("País: Czechoslovakia", "224"),
        ExcludeFilter("País: Democratic Republic of the Congo", "101"),
        ExcludeFilter("País: Denmark", "67"),
        ExcludeFilter("País: Djibouti", "68"),
        ExcludeFilter("País: Dominica", "69"),
        ExcludeFilter("País: Dominican Republic", "70"),
        ExcludeFilter("País: East Germany (GDR)", "57"),
        ExcludeFilter("País: Ecuador", "229"),
        ExcludeFilter("País: Egypt", "71"),
        ExcludeFilter("País: El Salvador", "171"),
        ExcludeFilter("País: Equatorial Guinea", "230"),
        ExcludeFilter("País: Eritrea", "231"),
        ExcludeFilter("País: Estonia", "232"),
        ExcludeFilter("País: Eswatini", "239"),
        ExcludeFilter("País: Ethiopia", "233"),
        ExcludeFilter("País: Falkland Islands", "215"),
        ExcludeFilter("País: Faroe Islands", "210"),
        ExcludeFilter("País: Federal Republic of Yugoslavia", "236"),
        ExcludeFilter("País: Federated States of Micronesia", "211"),
    )
}
