package ireader.presentation.ui.video.component.cores.player


import ireader.core.log.Log
import java.util.*


object SubtitleHelper {
    data class Language639(
        val languageName: String,
        val nativeName: String,
        val ISO_639_1: String,
        val ISO_639_2_T: String,
        val ISO_639_2_B: String,
        val ISO_639_3: String,
        val ISO_639_6: String,
    )


    /*fun createISO() {
        val url = "https://infogalactic.com/info/List_of_ISO_639-1_codes"
        val response = get(url).text
        val document = Jsoup.parse(response)
        val headers = document.select("table.wikitable > tbody > tr")

        var text = "listOf(\n"
        for (head in headers) {
            val tds = head.select("td")
            if (tds.size < 8) continue
            val name = tds[2].selectFirst("> a").text()
            val native = tds[3].text()
            val ISO_639_1 = tds[4].ownText().replace("+", "").replace(" ", "")
            val ISO_639_2_T = tds[5].ownText().replace("+", "").replace(" ", "")
            val ISO_639_2_B = tds[6].ownText().replace("+", "").replace(" ", "")
            val ISO_639_3 = tds[7].ownText().replace("+", "").replace(" ", "")
            val ISO_639_6 = tds[8].ownText().replace("+", "").replace(" ", "")

            val txtAdd =
                "Language(\"$name\", \"$native\", \"$ISO_639_1\", \"$ISO_639_2_T\", \"$ISO_639_2_B\", \"$ISO_639_3\", \"$ISO_639_6\"),\n"
            text += txtAdd
        }
        text += ")"
        println("ISO CREATED:\n$text")
    }*/

    /** lang -> ISO_639_1
     * @param looseCheck will use .contains in addition to .equals
     * */
    fun fromLanguageToTwoLetters(input: String, looseCheck: Boolean): String? {
        languages.forEach {
            if (it.languageName.equals(input, ignoreCase = true)
                || it.nativeName.equals(input, ignoreCase = true)
            ) return it.ISO_639_1
        }

        // Runs as a separate loop as to prioritize fully matching languages.
        if (looseCheck)
            languages.forEach {
                if (input.contains(it.languageName, ignoreCase = true)
                    || input.contains(it.nativeName, ignoreCase = true)
                ) return it.ISO_639_1
            }

        return null
    }

    private var ISO_639_1Map: HashMap<String, String> = hashMapOf()
    private fun initISO6391Map() {
        for (lang in languages) {
            ISO_639_1Map[lang.ISO_639_1] = lang.languageName
        }
    }
    /** ISO_639_1 -> lang*/
    fun fromTwoLettersToLanguage(input: String): String? {
        // pr-BR
        if (input.substringBefore("-").length != 2) return null
        if (ISO_639_1Map.isEmpty()) {
            initISO6391Map()
        }
        val comparison = input.lowercase(Locale.ROOT)

        return ISO_639_1Map[comparison]
    }

    /**ISO_639_2_B or ISO_639_2_T or ISO_639_3-> lang*/
    fun fromThreeLettersToLanguage(input: String): String? {
        if (input.length != 3) return null
        val comparison = input.lowercase(Locale.ROOT)
        for (lang in languages) {
            if (lang.ISO_639_2_B == comparison) {
                return lang.languageName
            }
        }
        for (lang in languages) {
            if (lang.ISO_639_2_T == comparison) {
                return lang.languageName
            }
        }
        for (lang in languages) {
            if (lang.ISO_639_3 == comparison) {
                return lang.languageName
            }
        }
        return null
    }

    /** lang -> ISO_639_2_T*/
    fun fromLanguageToThreeLetters(input: String): String? {
        for (lang in languages) {
            if (lang.languageName == input || lang.nativeName == input) {
                return lang.ISO_639_2_T
            }
        }
        return null
    }

    private const val flagOffset = 0x1F1E6
    private const val asciiOffset = 0x41
    private const val offset = flagOffset - asciiOffset

    private val flagRegex = Regex("[\uD83C\uDDE6-\uD83C\uDDFF]{2}")

    fun getFlagFromIso(inp: String?): String? {
        if (inp.isNullOrBlank() || inp.length < 2) return null

        try {
            val ret = getFlagFromIsoShort(flags[inp])
                ?: getFlagFromIsoShort(inp.uppercase()) ?: return null

            return if (flagRegex.matches(ret)) {
                ret
            } else {
                null
            }
        } catch (e: Exception) {
            Log.error(e)
            return null
        }
    }

    private fun getFlagFromIsoShort(flagAscii: String?): String? {
        if (flagAscii.isNullOrBlank() || flagAscii.length < 2) return null
        return try {
            val firstChar: Int = Character.codePointAt(flagAscii, 0) + offset
            val secondChar: Int = Character.codePointAt(flagAscii, 1) + offset

            (String(Character.toChars(firstChar)) + String(Character.toChars(secondChar)))
        } catch (e: Exception) {
            Log.error(e)
            null
        }
    }

    private val flags = mapOf(
        "af" to "ZA",
        "agq" to "CM",
        "ak" to "GH",
        "am" to "ET",
        "ar" to "AE",
        "as" to "IN",
        "asa" to "TZ",
        "az" to "AZ",
        "bas" to "CM",
        "be" to "BY",
        "bem" to "ZM",
        "bez" to "IT",
        "bg" to "BG",
        "bm" to "ML",
        "bn" to "BD",
        "bo" to "CN",
        "br" to "FR",
        "brx" to "IN",
        "bs" to "BA",
        "ca" to "ES",
        "cgg" to "UG",
        "chr" to "US",
        "cs" to "CZ",
        "cy" to "GB",
        "da" to "DK",
        "dav" to "KE",
        "de" to "DE",
        "dje" to "NE",
        "dua" to "CM",
        "dyo" to "SN",
        "ebu" to "KE",
        "ee" to "GH",
        "en" to "GB",
        "el" to "GR",
        "es" to "ES",
        "et" to "EE",
        "eu" to "ES",
        "ewo" to "CM",
        "fa" to "IR",
        "fil" to "PH",
        "fr" to "FR",
        "ga" to "IE",
        "gl" to "ES",
        "gsw" to "CH",
        "gu" to "IN",
        "guz" to "KE",
        "gv" to "GB",
        "ha" to "NG",
        "haw" to "US",
        "he" to "IL",
        "hi" to "IN",
        "ff" to "CN",
        "fi" to "FI",
        "fo" to "FO",
        "hr" to "HR",
        "hu" to "HU",
        "hy" to "AM",
        "id" to "ID",
        "ig" to "NG",
        "ii" to "CN",
        "is" to "IS",
        "it" to "IT",
        "ita" to "IT",
        "ja" to "JP",
        "jmc" to "TZ",
        "ka" to "GE",
        "kab" to "DZ",
        "ki" to "KE",
        "kam" to "KE",
        "mer" to "KE",
        "kde" to "TZ",
        "kea" to "CV",
        "khq" to "ML",
        "kk" to "KZ",
        "kl" to "GL",
        "kln" to "KE",
        "km" to "KH",
        "kn" to "IN",
        "ko" to "KR",
        "kok" to "IN",
        "ksb" to "TZ",
        "ksf" to "CM",
        "kw" to "GB",
        "lag" to "TZ",
        "lg" to "UG",
        "ln" to "CG",
        "lt" to "LT",
        "lu" to "CD",
        "lv" to "LV",
        "lat" to "LV",
        "luo" to "KE",
        "luy" to "KE",
        "mas" to "TZ",
        "mfe" to "MU",
        "mg" to "MG",
        "mgh" to "MZ",
        "ml" to "IN",
        "mk" to "MK",
        "mr" to "IN",
        "ms" to "MY",
        "mt" to "MT",
        "mua" to "CM",
        "my" to "MM",
        "naq" to "NA",
        "nb" to "NO",
        "no" to "NO",
        "nn" to "NO",
        "nd" to "ZW",
        "ne" to "NP",
        "nl" to "NL",
        "nmg" to "CM",
        "nus" to "SD",
        "nyn" to "UG",
        "om" to "ET",
        "or" to "IN",
        "pa" to "PK",
        "pl" to "PL",
        "ps" to "AF",
        "pt" to "PT",
        "pt-pt" to "PT",
        "pt-br" to "BR",
        "rm" to "CH",
        "rn" to "BI",
        "ro" to "RO",
        "ru" to "RU",
        "rw" to "RW",
        "rof" to "TZ",
        "rwk" to "TZ",
        "saq" to "KE",
        "sbp" to "TZ",
        "seh" to "MZ",
        "ses" to "ML",
        "sg" to "CF",
        "shi" to "MA",
        "si" to "LK",
        "sk" to "SK",
        "sl" to "SI",
        "sn" to "ZW",
        "so" to "SO",
        "sq" to "AL",
        "sr" to "RS",
        "sv" to "SE",
        "sw" to "TZ",
        "swc" to "CD",
        "ta" to "IN",
        "te" to "IN",
        "teo" to "UG",
        "th" to "TH",
        "ti" to "ET",
        "to" to "TO",
        "tr" to "TR",
        "twq" to "NE",
        "tzm" to "MA",
        "uk" to "UA",
        "ur" to "PK",
        "uz" to "UZ",
        "vai" to "LR",
        "vi" to "VN",
        "vun" to "TZ",
        "xog" to "UG",
        "yav" to "CM",
        "yo" to "NG",
        "zh" to "CN",
        "zu" to "ZA",
        "tl" to "PH",
    )

    val languages = listOf(
        Language639("Abkhaz", "?????????? ????????????, ????????????", "ab", "abk", "abk", "abk", "abks"),
        Language639("Afar", "Afaraf", "aa", "aar", "aar", "aar", "aars"),
        Language639("Afrikaans", "Afrikaans", "af", "afr", "afr", "afr", "afrs"),
        Language639("Akan", "Akan", "ak", "aka", "aka", "aka", ""),
        Language639("Albanian", "Shqip", "sq", "sqi", "", "sqi", ""),
        Language639("Amharic", "????????????", "am", "amh", "amh", "amh", ""),
        Language639("Arabic", "??????????????", "ar", "ara", "ara", "ara", ""),
        Language639("Aragonese", "aragon??s", "an", "arg", "arg", "arg", ""),
        Language639("Armenian", "??????????????", "hy", "hye", "", "hye", ""),
        Language639("Assamese", "?????????????????????", "as", "asm", "asm", "asm", ""),
        Language639("Avaric", "???????? ????????, ???????????????? ????????", "av", "ava", "ava", "ava", ""),
        Language639("Avestan", "avesta", "ae", "ave", "ave", "ave", ""),
        Language639("Aymara", "aymar aru", "ay", "aym", "aym", "aym", ""),
        Language639("Azerbaijani", "az??rbaycan dili", "az", "aze", "aze", "aze", ""),
        Language639("Bambara", "bamanankan", "bm", "bam", "bam", "bam", ""),
        Language639("Bashkir", "?????????????? ????????", "ba", "bak", "bak", "bak", ""),
        Language639("Basque", "euskara, euskera", "eu", "eus", "", "eus", ""),
        Language639("Belarusian", "???????????????????? ????????", "be", "bel", "bel", "bel", ""),
        Language639("Bengali", "???????????????", "bn", "ben", "ben", "ben", ""),
        Language639("Bihari", "?????????????????????", "bh", "bih", "bih", "", ""),
        Language639("Bislama", "Bislama", "bi", "bis", "bis", "bis", ""),
        Language639("Bosnian", "bosanski jezik", "bs", "bos", "bos", "bos", "boss"),
        Language639("Breton", "brezhoneg", "br", "bre", "bre", "bre", ""),
        Language639("Bulgarian", "?????????????????? ????????", "bg", "bul", "bul", "bul", "buls"),
        Language639("Burmese", "???????????????", "my", "mya", "", "mya", ""),
        Language639("Catalan", "catal??", "ca", "cat", "cat", "cat", ""),
        Language639("Chamorro", "Chamoru", "ch", "cha", "cha", "cha", ""),
        Language639("Chechen", "?????????????? ????????", "ce", "che", "che", "che", ""),
        Language639("Chichewa", "chiChe??a, chinyanja", "ny", "nya", "nya", "nya", ""),
        Language639("Chinese", "?????? (Zh??ngw??n), ??????, ??????", "zh", "zho", "", "zho", ""),
        Language639("Chuvash", "?????????? ??????????", "cv", "chv", "chv", "chv", ""),
        Language639("Cornish", "Kernewek", "kw", "cor", "cor", "cor", ""),
        Language639("Corsican", "corsu, lingua corsa", "co", "cos", "cos", "cos", ""),
        Language639("Cree", "?????????????????????", "cr", "cre", "cre", "cre", ""),
        Language639("Croatian", "hrvatski jezik", "hr", "hrv", "hrv", "hrv", ""),
        Language639("Czech", "??e??tina, ??esk?? jazyk", "cs", "ces", "", "ces", ""),
        Language639("Danish", "dansk", "da", "dan", "dan", "dan", ""),
        Language639("Divehi", "????????????", "dv", "div", "div", "div", ""),
        Language639("Dutch", "Nederlands, Vlaams", "nl", "nld", "", "nld", ""),
        Language639("Dzongkha", "??????????????????", "dz", "dzo", "dzo", "dzo", ""),
        Language639("English", "English", "en", "eng", "eng", "eng", "engs"),
        Language639("Esperanto", "Esperanto", "eo", "epo", "epo", "epo", ""),
        Language639("Estonian", "eesti, eesti keel", "et", "est", "est", "est", ""),
        Language639("Ewe", "E??egbe", "ee", "ewe", "ewe", "ewe", ""),
        Language639("Faroese", "f??royskt", "fo", "fao", "fao", "fao", ""),
        Language639("Fijian", "vosa Vakaviti", "fj", "fij", "fij", "fij", ""),
        Language639("Finnish", "suomi, suomen kieli", "fi", "fin", "fin", "fin", ""),
        Language639("French", "fran??ais, langue fran??aise", "fr", "fra", "", "fra", "fras"),
        Language639("Fula", "Fulfulde, Pulaar, Pular", "ff", "ful", "ful", "ful", ""),
        Language639("Galician", "galego", "gl", "glg", "glg", "glg", ""),
        Language639("Georgian", "?????????????????????", "ka", "kat", "", "kat", ""),
        Language639("German", "Deutsch", "de", "deu", "", "deu", "deus"),
        Language639("Greek", "????????????????", "el", "ell", "", "ell", "ells"),
        Language639("Guaran??", "Ava??e'???", "gn", "grn", "grn", "grn", ""),
        Language639("Gujarati", "?????????????????????", "gu", "guj", "guj", "guj", ""),
        Language639("Haitian", "Krey??l ayisyen", "ht", "hat", "hat", "hat", ""),
        Language639("Hausa", "(Hausa) ????????????", "ha", "hau", "hau", "hau", ""),
        Language639("Hebrew", "??????????", "he", "heb", "heb", "heb", ""),
        Language639("Herero", "Otjiherero", "hz", "her", "her", "her", ""),
        Language639("Hindi", "??????????????????, ???????????????", "hi", "hin", "hin", "hin", "hins"),
        Language639("Hiri Motu", "Hiri Motu", "ho", "hmo", "hmo", "hmo", ""),
        Language639("Hungarian", "magyar", "hu", "hun", "hun", "hun", ""),
        Language639("Interlingua", "Interlingua", "ia", "ina", "ina", "ina", ""),
        Language639("Indonesian", "Bahasa Indonesia", "id", "ind", "ind", "ind", ""),
        Language639(
            "Interlingue",
            "Originally called Occidental; then Interlingue after WWII",
            "ie",
            "ile",
            "ile",
            "ile",
            ""
        ),
        Language639("Irish", "Gaeilge", "ga", "gle", "gle", "gle", ""),
        Language639("Igbo", "As???s??? Igbo", "ig", "ibo", "ibo", "ibo", ""),
        Language639("Inupiaq", "I??upiaq, I??upiatun", "ik", "ipk", "ipk", "ipk", ""),
        Language639("Ido", "Ido", "io", "ido", "ido", "ido", "idos"),
        Language639("Icelandic", "??slenska", "is", "isl", "", "isl", ""),
        Language639("Italian", "italiano", "it", "ita", "ita", "ita", "itas"),
        Language639("Inuktitut", "??????????????????", "iu", "iku", "iku", "iku", ""),
        Language639("Japanese", "????????? (????????????)", "ja", "jpn", "jpn", "jpn", ""),
        Language639("Javanese", "????????????", "jv", "jav", "jav", "jav", ""),
        Language639("Kalaallisut", "kalaallisut, kalaallit oqaasii", "kl", "kal", "kal", "kal", ""),
        Language639("Kannada", "???????????????", "kn", "kan", "kan", "kan", ""),
        Language639("Kanuri", "Kanuri", "kr", "kau", "kau", "kau", ""),
        Language639("Kashmiri", "?????????????????????, ???????????????", "ks", "kas", "kas", "kas", ""),
        Language639("Kazakh", "?????????? ????????", "kk", "kaz", "kaz", "kaz", ""),
        Language639("Khmer", "???????????????, ????????????????????????, ???????????????????????????", "km", "khm", "khm", "khm", ""),
        Language639("Kikuyu", "G??k??y??", "ki", "kik", "kik", "kik", ""),
        Language639("Kinyarwanda", "Ikinyarwanda", "rw", "kin", "kin", "kin", ""),
        Language639("Kyrgyz", "????????????????, ???????????? ????????", "ky", "kir", "kir", "kir", ""),
        Language639("Komi", "???????? ??????", "kv", "kom", "kom", "kom", ""),
        Language639("Kongo", "Kikongo", "kg", "kon", "kon", "kon", ""),
        Language639("Korean", "?????????, ?????????", "ko", "kor", "kor", "kor", ""),
        Language639("Kurdish", "Kurd??, ?????????????", "ku", "kur", "kur", "kur", ""),
        Language639("Kwanyama", "Kuanyama", "kj", "kua", "kua", "kua", ""),
        Language639("Latin", "latine, lingua latina", "la", "lat", "lat", "lat", "lats"),
        Language639("Luxembourgish", "L??tzebuergesch", "lb", "ltz", "ltz", "ltz", ""),
        Language639("Ganda", "Luganda", "lg", "lug", "lug", "lug", ""),
        Language639("Limburgish", "Limburgs", "li", "lim", "lim", "lim", ""),
        Language639("Lingala", "Ling??la", "ln", "lin", "lin", "lin", ""),
        Language639("Lao", "?????????????????????", "lo", "lao", "lao", "lao", ""),
        Language639("Lithuanian", "lietuvi?? kalba", "lt", "lit", "lit", "lit", ""),
        Language639("Luba-Katanga", "Tshiluba", "lu", "lub", "lub", "lub", ""),
        Language639("Latvian", "latvie??u valoda", "lv", "lav", "lav", "lav", ""),
        Language639("Manx", "Gaelg, Gailck", "gv", "glv", "glv", "glv", ""),
        Language639("Macedonian", "???????????????????? ??????????", "mk", "mkd", "", "mkd", ""),
        Language639("Malagasy", "fiteny malagasy", "mg", "mlg", "mlg", "mlg", ""),
        Language639("Malay", "bahasa Melayu, ???????? ?????????????", "ms", "msa", "", "msa", ""),
        Language639("Malayalam", "??????????????????", "ml", "mal", "mal", "mal", ""),
        Language639("Maltese", "Malti", "mt", "mlt", "mlt", "mlt", ""),
        Language639("M??ori", "te reo M??ori", "mi", "mri", "", "mri", ""),
        Language639("Marathi", "???????????????", "mr", "mar", "mar", "mar", ""),
        Language639("Marshallese", "Kajin M??aje??", "mh", "mah", "mah", "mah", ""),
        Language639("Mongolian", "???????????? ??????", "mn", "mon", "mon", "mon", ""),
        Language639("Nauruan", "Dorerin Naoero", "na", "nau", "nau", "nau", ""),
        Language639("Navajo", "Din?? bizaad", "nv", "nav", "nav", "nav", ""),
        Language639("Northern Ndebele", "isiNdebele", "nd", "nde", "nde", "nde", ""),
        Language639("Nepali", "??????????????????", "ne", "nep", "nep", "nep", ""),
        Language639("Ndonga", "Owambo", "ng", "ndo", "ndo", "ndo", ""),
        Language639("Norwegian Bokm??l", "Norsk bokm??l", "nb", "nob", "nob", "nob", ""),
        Language639("Norwegian Nynorsk", "Norsk nynorsk", "nn", "nno", "nno", "nno", ""),
        Language639("Norwegian", "Norsk", "no", "nor", "nor", "nor", ""),
        Language639("Nuosu", "????????? Nuosuhxop", "ii", "iii", "iii", "iii", ""),
        Language639("Southern Ndebele", "isiNdebele", "nr", "nbl", "nbl", "nbl", ""),
        Language639("Occitan", "occitan, lenga d'??c", "oc", "oci", "oci", "oci", ""),
        Language639("Ojibwe", "????????????????????????", "oj", "oji", "oji", "oji", ""),
        Language639("Old Church Slavonic", "?????????? ????????????????????", "cu", "chu", "chu", "chu", ""),
        Language639("Oromo", "Afaan Oromoo", "om", "orm", "orm", "orm", ""),
        Language639("Oriya", "???????????????", "or", "ori", "ori", "ori", ""),
        Language639("Ossetian", "???????? ??????????", "os", "oss", "oss", "oss", ""),
        Language639("Panjabi", "??????????????????, ???????????????", "pa", "pan", "pan", "pan", ""),
        Language639("P??li", "????????????", "pi", "pli", "pli", "pli", ""),
        Language639("Persian", "??????????", "fa", "fas", "", "fas", ""),
        Language639("Polish", "j??zyk polski, polszczyzna", "pl", "pol", "pol", "pol", "pols"),
        Language639("Pashto", "????????", "ps", "pus", "pus", "pus", ""),
        Language639("Portuguese", "portugu??s", "pt-pt", "por", "por", "por", ""),
        // Addition to support Brazilian Portuguese properly, might break other things
        Language639("Portuguese (Brazilian)", "portugu??s", "pt-br", "por", "por", "por", ""),
        Language639("Quechua", "Runa Simi, Kichwa", "qu", "que", "que", "que", ""),
        Language639("Romansh", "rumantsch grischun", "rm", "roh", "roh", "roh", ""),
        Language639("Kirundi", "Ikirundi", "rn", "run", "run", "run", ""),
        Language639("Reunion Creole", "Kr??ol R??nion??", "rc", "rcf", "rcf", "rcf", ""),
        Language639("Romanian", "limba rom??n??", "ro", "ron", "", "ron", ""),
        Language639("Russian", "??????????????", "ru", "rus", "rus", "rus", ""),
        Language639("Sanskrit", "???????????????????????????", "sa", "san", "san", "san", ""),
        Language639("Sardinian", "sardu", "sc", "srd", "srd", "srd", ""),
        Language639("Sindhi", "??????????????????, ?????????? ?????????????", "sd", "snd", "snd", "snd", ""),
        Language639("Northern Sami", "Davvis??megiella", "se", "sme", "sme", "sme", ""),
        Language639("Samoan", "gagana fa'a Samoa", "sm", "smo", "smo", "smo", ""),
        Language639("Sango", "y??ng?? t?? s??ng??", "sg", "sag", "sag", "sag", ""),
        Language639("Serbian", "???????????? ??????????", "sr", "srp", "srp", "srp", ""),
        Language639("Scottish Gaelic", "G??idhlig", "gd", "gla", "gla", "gla", ""),
        Language639("Shona", "chiShona", "sn", "sna", "sna", "sna", ""),
        Language639("Sinhalese", "???????????????", "si", "sin", "sin", "sin", ""),
        Language639("Slovak", "sloven??ina, slovensk?? jazyk", "sk", "slk", "", "slk", ""),
        Language639("Slovene", "slovenski jezik, sloven????ina", "sl", "slv", "slv", "slv", ""),
        Language639("Somali", "Soomaaliga, af Soomaali", "so", "som", "som", "som", ""),
        Language639("Southern Sotho", "Sesotho", "st", "sot", "sot", "sot", ""),
        Language639("Spanish", "espa??ol", "es", "spa", "spa", "spa", ""),
        Language639("Sundanese", "Basa Sunda", "su", "sun", "sun", "sun", ""),
        Language639("Swahili", "Kiswahili", "sw", "swa", "swa", "swa", ""),
        Language639("Swati", "SiSwati", "ss", "ssw", "ssw", "ssw", ""),
        Language639("Swedish", "svenska", "sv", "swe", "swe", "swe", ""),
        Language639("Tamil", "???????????????", "ta", "tam", "tam", "tam", ""),
        Language639("Telugu", "??????????????????", "te", "tel", "tel", "tel", ""),
        Language639("Tajik", "????????????, to??ik??, ???????????????", "tg", "tgk", "tgk", "tgk", ""),
        Language639("Thai", "?????????", "th", "tha", "tha", "tha", ""),
        Language639("Tigrinya", "????????????", "ti", "tir", "tir", "tir", ""),
        Language639("Tibetan Standard", "?????????????????????", "bo", "bod", "", "bod", ""),
        Language639("Turkmen", "T??rkmen, ??????????????", "tk", "tuk", "tuk", "tuk", ""),
        Language639("Tagalog", "Wikang Tagalog", "tl", "tgl", "tgl", "tgl", ""),
        Language639("Tswana", "Setswana", "tn", "tsn", "tsn", "tsn", ""),
        Language639("Tonga", "faka Tonga", "to", "ton", "ton", "ton", ""),
        Language639("Turkish", "T??rk??e", "tr", "tur", "tur", "tur", ""),
        Language639("Tsonga", "Xitsonga", "ts", "tso", "tso", "tso", ""),
        Language639("Tatar", "?????????? ????????, tatar tele", "tt", "tat", "tat", "tat", ""),
        Language639("Twi", "Twi", "tw", "twi", "twi", "twi", ""),
        Language639("Tahitian", "Reo Tahiti", "ty", "tah", "tah", "tah", ""),
        Language639("Uyghur", "???????????????????, Uyghurche", "ug", "uig", "uig", "uig", ""),
        Language639("Ukrainian", "????????????????????", "uk", "ukr", "ukr", "ukr", ""),
        Language639("Urdu", "????????", "ur", "urd", "urd", "urd", ""),
        Language639("Uzbek", "O??zbek, ??????????, ???????????????", "uz", "uzb", "uzb", "uzb", ""),
        Language639("Venda", "Tshiven???a", "ve", "ven", "ven", "ven", ""),
        Language639("Vietnamese", "Ti???ng Vi???t", "vi", "vie", "vie", "vie", ""),
        Language639("Volap??k", "Volap??k", "vo", "vol", "vol", "vol", ""),
        Language639("Walloon", "walon", "wa", "wln", "wln", "wln", ""),
        Language639("Welsh", "Cymraeg", "cy", "cym", "", "cym", ""),
        Language639("Wolof", "Wollof", "wo", "wol", "wol", "wol", ""),
        Language639("Western Frisian", "Frysk", "fy", "fry", "fry", "fry", ""),
        Language639("Xhosa", "isiXhosa", "xh", "xho", "xho", "xho", ""),
        Language639("Yiddish", "????????????", "yi", "yid", "yid", "yid", ""),
        Language639("Yoruba", "Yor??b??", "yo", "yor", "yor", "yor", ""),
        Language639("Zhuang", "Sa?? cue????, Saw cuengh", "za", "zha", "zha", "zha", ""),
        Language639("Zulu", "isiZulu", "zu", "zul", "zul", "zul", ""),
    )
}