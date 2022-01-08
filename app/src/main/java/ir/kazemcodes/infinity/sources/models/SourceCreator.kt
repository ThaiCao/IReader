package ir.kazemcodes.infinity.sources.models

import android.content.Context
import android.util.Patterns
import com.nfeld.jsonpathkt.JsonPath
import com.nfeld.jsonpathkt.extension.read
import ir.kazemcodes.infinity.api_feature.network.GET
import ir.kazemcodes.infinity.api_feature.network.POST
import ir.kazemcodes.infinity.data.network.models.*
import ir.kazemcodes.infinity.domain.models.remote.Book
import ir.kazemcodes.infinity.domain.models.remote.Chapter
import ir.kazemcodes.infinity.presentation.book_detail.Constants
import ir.kazemcodes.infinity.util.*
import okhttp3.Headers
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element


class SourceCreator(
    context: Context,
    _baseUrl: String,
    private val _lang: String,
    private val _name: String,
    private val _supportsMostPopular: Boolean = false,
    private val _supportsSearch: Boolean = false,
    private val _supportsLatest: Boolean = false,
    private val latest: Latest? = null,
    private val popular: Popular? = null,
    private val detail: Detail? = null,
    private val search: Search? = null,
    private val chapters: Chapters? = null,
    private val content: Content? = null,
) : ParsedHttpSource(context) {
    override val lang: String
        get() = _lang
    override val name: String
        get() = _name
    override val supportsLatest: Boolean
        get() = _supportsLatest
    override val supportsMostPopular: Boolean
        get() = _supportsMostPopular
    override val baseUrl: String = _baseUrl

    override val supportSearch: Boolean = _supportsSearch


    override fun headersBuilder(): Headers.Builder = Headers.Builder().apply {
        add(
            "User-Agent",
            "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.110 Mobile Safari/537.36 "
        )
        add("Referer", baseUrl)
        add("cache-control", "max-age=0")
    }

    override val fetchLatestEndpoint: String? = latest?.endpoint
    override val fetchPopularEndpoint: String? = popular?.endpoint
    override val fetchSearchEndpoint: String? = search?.endpoint
    override val fetchChaptersEndpoint: String? = chapters?.endpoint
    override val fetchContentEndpoint: String? = content?.endpoint


    /****************************SELECTOR*************************************************************/
    override val popularSelector: String? = popular?.selector

    override fun popularBookNextPageSelector(): String? = popular?.nextBookSelector
    override val latestSelector: String? = latest?.selector
    override val latestUpdatesNextPageSelector: String? = latest?.nextPageSelector
    override fun hasNextChapterSelector() = chapters?.hasNextChapterListSelector
    override val searchSelector: String? = search?.selector
    override fun searchBookNextPageSelector(): String? =
        search?.hasNextSearchedBooksNextPageSelector

    override fun searchBookNextValuePageSelector(): String? =
        search?.hasNextSearchedBookNextPageValue

    override val chaptersSelector: String? = chapters?.selector
    override val ajaxChaptersSelector: String? = chapters?.ajaxSelector
    override val ajaxContentSelector: String? = content?.ajaxSelector
    override val ajaxLatestSelector: String? = latest?.ajaxSelector
    override val ajaxPopularSelector: String? = popular?.ajaxSelector
    override val ajaxDetailSelector: String? = detail?.ajaxSelector

    /****************************SELECTOR*************************************************************/

    var nextChapterListLink: String = ""

    /****************************REQUESTS**********************************************************/

    override fun popularRequest(page: Int): Request =
        GET("$baseUrl${
            getUrlWithoutDomain(fetchPopularEndpoint?.applyPageFormat(page) ?: "")
        }")

    override fun latestRequest(page: Int): Request {
        val url = fetchLatestEndpoint?.applyPageFormat(page) ?: ""
        return GET("$baseUrl${
            getUrlWithoutDomain(url)
        }")
    }

    override fun chaptersRequest(book: Book, page: Int): Request {
        var url = book.link
        /** This condition occurs when the next chapter selector returns a link to the next chapter**/
        if (nextChapterListLink.isNotBlank()) {
            url = nextChapterListLink
        }
        if (!chapters?.endpoint.isNullOrEmpty()) {
            url = book.link.replace(chapters?.chaptersEndpointWithoutPage
                ?: "", (chapters?.endpoint ?: "").replace(pageFormat, page.toString()))
        }
        if (chapters?._shouldStringSomethingAtEnd == true && !chapters._subStringSomethingAtEnd.isNullOrEmpty()) {
            url = book.link + chapters._subStringSomethingAtEnd
        }
        if (chapters?.isHtmlType == true) {
            return GET(baseUrl + getUrlWithoutDomain(url))
        } else {
            return POST(baseUrl + getUrlWithoutDomain(url))
        }
    }

    override fun searchRequest(page: Int, query: String): Request {

        if (search?.isHtmlType == true) {
            return GET("$baseUrl${
                getUrlWithoutDomain(fetchSearchEndpoint?.replace(searchQueryFormat,
                    query) ?: "")
            }")
        } else {
            return POST(
                "$baseUrl${
                    getUrlWithoutDomain(fetchSearchEndpoint?.replace(searchQueryFormat,
                        query) ?: "")
                }")
        }

    }
    /****************************REQUESTS**********************************************************/


    /****************************PARSE FROM ELEMENTS***********************************************/


    override fun popularFromElement(element: Element): Book {
        val book: Book = Book.create()

        val selectorLink = popular?.linkSelector
        val attLink = popular?.linkAtt
        val selectorName = popular?.nameSelector
        val attName = popular?.nameAtt
        val selectorCover = popular?.coverSelector
        val attCover = popular?.coverAtt


        book.link = baseUrl + getUrlWithoutDomain(selectorReturnerStringType(element,
            selectorLink,
            attLink).shouldSubstring(popular?.linkSubString, baseUrl))
        book.bookName = selectorReturnerStringType(element, selectorName, attName)
        book.coverLink = selectorReturnerStringType(element, selectorCover, attCover)


        return book
    }


    override fun latestFromElement(element: Element): Book {
        val book: Book = Book.create()

        val selectorLink = latest?.linkSelector
        val attLink = latest?.linkAtt
        val selectorName = latest?.nameSelector
        val attName = latest?.nameAtt
        val selectorCover = latest?.coverSelector
        val attCover = latest?.coverAtt

        book.link = baseUrl + getUrlWithoutDomain(selectorReturnerStringType(element,
            selectorLink,
            attLink))
        book.bookName = selectorReturnerStringType(element, selectorName, attName)
        book.coverLink = selectorReturnerStringType(element, selectorCover, attCover)


        //Timber.e("Timber: SourceCreator" + book.coverLink)

        return book
    }

    override fun chapterFromElement(element: Element): Chapter {
        val chapter = Chapter.create()

        val selectorLink = chapters?.linkSelector
        val attLink = chapters?.linkAtt
        val selectorName = chapters?.nameSelector
        val attName = chapters?.nameAtt

        chapter.link = baseUrl + getUrlWithoutDomain(selectorReturnerStringType(element,
            selectorLink,
            attLink))
        chapter.title = selectorReturnerStringType(element, selectorName, attName)
        chapter.haveBeenRead = false


        return chapter
    }

    override fun searchFromElement(element: Element): Book {
        val book: Book = Book.create()
        val selectorLink = search?.linkSelector
        val attLink = search?.linkAtt
        val selectorName = search?.nameSelector
        val attName = search?.nameAtt
        val selectorCover = search?.coverSelector
        val attCover = search?.coverAtt

        book.link = baseUrl + getUrlWithoutDomain(selectorReturnerStringType(element,
            selectorLink,
            attLink))
        book.bookName = selectorReturnerStringType(element, selectorName, attName)
        book.coverLink = selectorReturnerStringType(element, selectorCover, attCover)

        return book
    }
    /****************************PARSE FROM ELEMENTS***********************************************/

    /****************************PARSE*************************************************************/

    override fun latestParse(document: Document, page: Int): BooksPage {
        val isCloudflareEnable =
            document.body().allElements.text().contains(Constants.CLOUDFLARE_LOG)

        val ajaxLoaded = ajaxChaptersSelector.isNull() || (selectorReturnerStringType(document,
            ajaxChaptersSelector).isNotEmpty() && ajaxChaptersSelector.isNotNull())
        val books = mutableListOf<Book>()
        if (this.latest?.isHtmlType == true) {
            books.addAll(document.select(latest?.selector).map { element ->
                latestFromElement(element)
            })
        } else {
            val crudeJson = Jsoup.parse(document.html()).text().trim()
            val json = JsonPath.parse(crudeJson)
            val selector = json?.read<List<Map<String, String>>>(this.latest?.selector ?: "")
            selector?.forEach { jsonObject ->
                books.add(latestFromJson(jsonObject))
            }
        }

        val hasNextPage = latestUpdatesNextPageSelector?.let { selector ->
            document.select(selector).first()
        } != null
        if (latest?.supportPageList == true) {
            nextChapterListLink = parseNextChapterListType(document, page)
        }

        return BooksPage(books,
            hasNextPage,
            isCloudflareEnable,
            document.body().allElements.text(),
            ajaxLoaded = ajaxLoaded)
    }

    override fun detailParse(document: Document): BookPage {
        val isCloudflareEnable =
            document.body().allElements.text().contains(Constants.CLOUDFLARE_LOG)

        val ajaxLoaded = ajaxChaptersSelector.isNull() || (selectorReturnerStringType(document,
            ajaxChaptersSelector).isNotEmpty() && ajaxChaptersSelector.isNotNull())
        var book = Book.create()
        if (this.detail?.isHtmlType == true) {
            val selectorBookName = detail?.nameSelector
            val attBookName = detail?.nameAtt
            val coverSelector = detail?.coverSelector
            val coverAtt = detail?.coverAtt
            val selectorDescription = detail?.descriptionSelector
            val attDescription = detail?.descriptionBookAtt
            val selectorAuthor = detail?.authorBookSelector
            val attAuthor = detail?.authorBookAtt
            val selectorCategory = detail?.categorySelector
            val attCategory = detail?.categoryAtt



            book.bookName = selectorReturnerStringType(document, selectorBookName, attAuthor)
            book.coverLink = selectorReturnerStringType(document, coverSelector, coverAtt)
            book.author = selectorReturnerStringType(document, selectorAuthor, attBookName)
            book.description =
                selectorReturnerListType(document, selectorDescription, attDescription)
            book.category = selectorReturnerListType(document, selectorCategory, attCategory)
        } else {
            val crudeJson = Jsoup.parse(document.html()).text().trim()
            val json = JsonPath.parse(crudeJson)
            val selector = json?.read<List<Map<String, String>>>(this.detail?.selector ?: "")
            selector?.forEach { jsonObject ->
                book = detailFromJson(jsonObject)
            }
        }
        book.source = name


        return BookPage(book, isCloudflareEnabled = isCloudflareEnable, ajaxLoaded = ajaxLoaded)
    }


    override fun chaptersParse(document: Document): ChaptersPage {
        val isCloudflareEnable =
            document.body().allElements.text().contains(Constants.CLOUDFLARE_LOG)
        val ajaxLoaded = ajaxChaptersSelector.isNull() || (selectorReturnerStringType(document,
            ajaxChaptersSelector).isNotEmpty() && ajaxChaptersSelector.isNotNull())

        val chapters = mutableListOf<Chapter>()
        if (this.chapters?.isHtmlType == true) {

            chapters.addAll(document.select(chaptersSelector).map { chapterFromElement(it) })
        } else {
            val crudeJson = Jsoup.parse(document.html()).text().trim()
            val json = JsonPath.parse(crudeJson)
            val selector = json?.read<List<Map<String, String>>>(this.chapters?.selector ?: "")
            selector?.forEach { jsonObject ->
                chapters.add(chapterListFromJson(jsonObject))
            }
        }

        val hasNext = hasNextChaptersParse(document)


        return ChaptersPage(chapters,
            hasNext,
            isCloudflareEnabled = isCloudflareEnable,
            ajaxLoaded = ajaxLoaded)
    }

    override fun contentParse(document: Document): ChapterPage {
        val isCloudflareEnable =
            document.body().allElements.text().contains(Constants.CLOUDFLARE_LOG)
        val ajaxLoaded = ajaxChaptersSelector.isNull() || (selectorReturnerStringType(document,
            ajaxChaptersSelector).isNotEmpty() && ajaxChaptersSelector.isNotNull())
        val contentList: MutableList<String> = mutableListOf()
        if (content?.isHtmlType == true) {
            val contentSelector = content?.pageContentSelector
            val contentAtt = content?.pageContentAtt
            val titleSelector = content?.pageTitleSelector
            val titleAtt = content?.pageTitleAtt
            val title = selectorReturnerStringType(document, titleSelector, titleAtt)
            val page = selectorReturnerListType(document, contentSelector, contentAtt)
            contentList.add(title)
            contentList.addAll(page)
        } else {
            val crudeJson = Jsoup.parse(document.html()).text().trim()
            val json = JsonPath.parse(crudeJson)
            val selector = json?.read<List<Map<String, String>>>(this.chapters?.selector ?: "")
            selector?.forEach { jsonObject ->
                contentList.addAll(contentFromJson(jsonObject).content)
            }
        }
        return ChapterPage(contentList,
            isCloudflareEnabled = isCloudflareEnable,
            ajaxLoaded = ajaxLoaded)
    }

    override fun searchParse(document: Document, page: Int): BooksPage {
        val isCloudflareEnable =
            document.body().allElements.text().contains(Constants.CLOUDFLARE_LOG)
        val ajaxLoaded = ajaxChaptersSelector.isNull() || (selectorReturnerStringType(document,
            ajaxChaptersSelector).isNotEmpty() && ajaxChaptersSelector.isNotNull())
        var books = mutableListOf<Book>()

        if (search?.isHtmlType == true) {
            /**
             * I Add Filter Because sometimes this value contains null values
             * so the null book shows in search screen
             */
            books.addAll(document.select(searchSelector).map { element ->
                searchFromElement(element)
            }.filter {
                it.bookName.isNotBlank()
            })
        } else {
            val crudeJson = Jsoup.parse(document.html()).text().trim()
            val json = JsonPath.parse(crudeJson)
            val selector = json?.read<List<Map<String, String>>>(search?.selector ?: "")
            selector?.forEach { jsonObject ->
                books.add(searchBookFromJson(jsonObject))
            }
        }
        val hasNextPage = false

        return BooksPage(books, hasNextPage, isCloudflareEnable, ajaxLoaded = ajaxLoaded)
    }

    /****************************PARSE FROM JSON**********************************/
    fun chapterListFromJson(jsonObject: Map<String, String>): Chapter {
        val chapter = Chapter.create()
        val mName = chapters?.nameSelector
        val mLink = chapters?.linkSelector
        chapter.title = jsonObject[mName]?.replace("</strong>", "") ?: ""
        chapter.link = jsonObject[mLink] ?: ""
        chapter.haveBeenRead = false
        return chapter
    }

    fun searchBookFromJson(jsonObject: Map<String, String>): Book {
        val book = Book.create()
        val mName = search?.nameSelector
        val mLink = search?.linkSelector
        val mCover = search?.coverSelector
        book.bookName = jsonObject[mName]?.replace("</strong>", "") ?: ""
        book.link = jsonObject[mLink] ?: ""
        book.coverLink = jsonObject[mCover]
        return book
    }

    fun detailFromJson(jsonObject: Map<String, String>): Book {
        val book = Book.create()
        val mName = detail?.nameSelector
        val mCover = detail?.coverSelector
        val mDescription = detail?.descriptionSelector
        val mAuthor = detail?.authorBookSelector
        val mCategory = detail?.categorySelector

        book.bookName = jsonObject[mName]?.replace("</strong>", "") ?: ""
        book.coverLink = jsonObject[mCover]
        book.description = listOf(jsonObject[mDescription] ?: "")
        book.author = jsonObject[mAuthor]
        book.category = listOf(jsonObject[mCategory] ?: "")


        return book
    }

    fun latestFromJson(jsonObject: Map<String, String>): Book {
        val book = Book.create()
        val mName = search?.nameSelector
        val mLink = search?.linkSelector
        val mCover = search?.coverSelector
        book.bookName = jsonObject[mName]?.replace("</strong>", "") ?: ""
        book.link = jsonObject[mLink] ?: ""
        book.coverLink = jsonObject[mCover]
        return book
    }

    fun popularFromJson(jsonObject: Map<String, String>): Book {
        val book = Book.create()
        val mName = search?.nameSelector
        val mLink = search?.linkSelector
        val mCover = search?.coverSelector
        book.bookName = jsonObject[mName]?.replace("</strong>", "") ?: ""
        book.link = jsonObject[mLink] ?: ""
        book.coverLink = jsonObject[mCover]
        return book
    }

    fun contentFromJson(jsonObject: Map<String, String>): Chapter {
        val chapter = Chapter.create()
        val mContent = content?.pageContentSelector

        chapter.content = listOf(jsonObject[mContent]?.replace("</strong>", "") ?: "")

        return chapter
    }

    /****************************PARSE FROM JSON**********************************/


    override fun hasNextChaptersParse(document: Document): Boolean {
        if (chapters?.supportNextPagesList == true) {
            val docs = selectorReturnerStringType(document,
                chapters.hasNextChapterListSelector,
                chapters.hasNextChapterListAtt).shouldSubstring(chapters._shouldSubstringBaseUrlAtFirst
                ?: false,
                baseUrl,
                ::getUrlWithoutDomain)
            val condition =
                Patterns.WEB_URL.matcher(docs)
                    .matches() || docs.contains(chapters.hasNextChapterListValue
                    ?: "")
            if (Patterns.WEB_URL.matcher(docs).matches()) {
                nextChapterListLink = baseUrl + getUrlWithoutDomain(docs)
            }
            return condition
        } else {
            return false
        }
    }

    fun parseNextChapterListType(document: Document, page: Int): String {
        val selector = latest?.nextPageLinkSelector
        val att = latest?.nextPageLinkAtt
        val maxIndex = latest?.maxPageIndex
        val urlList = selectorReturnerListType(document,
            selector = selector,
            att)
        return urlList[page % (maxIndex ?: 0)]
    }


}