package org.ireader.presentation.feature_settings.presentation.webview

import android.annotation.SuppressLint
import android.webkit.WebView
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.ireader.core.utils.Constants
import org.ireader.core.utils.UiEvent
import org.ireader.core.utils.UiText
import org.ireader.core.utils.getHtml
import org.ireader.domain.R
import org.ireader.domain.models.entities.Book
import org.ireader.domain.models.entities.Chapter
import org.ireader.domain.models.source.FetchType
import org.ireader.domain.models.source.Source
import org.ireader.domain.source.Extensions
import org.ireader.domain.ui.NavigationArgs
import org.ireader.domain.use_cases.fetchers.FetchUseCase
import org.ireader.domain.use_cases.local.DeleteUseCase
import org.ireader.domain.use_cases.local.LocalGetChapterUseCase
import org.ireader.domain.use_cases.local.LocalInsertUseCases
import org.ireader.domain.utils.Resource
import org.ireader.domain.view_models.settings.webview.mapFetcher
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject

/**This is fake Alert **/
@SuppressLint("StaticFieldLeak")
@HiltViewModel
class WebViewPageModel @Inject constructor(
    private val insetUseCases: LocalInsertUseCases,
    private val deleteUseCase: DeleteUseCase,
    private val getBookUseCases: org.ireader.domain.use_cases.local.LocalGetBookUseCases,
    private val getChapterUseCase: LocalGetChapterUseCase,
    private val fetcherUseCase: FetchUseCase,
    private val webView: WebView,
    private val savedStateHandle: SavedStateHandle,
    private val extensions: Extensions,
) : ViewModel() {

    private val _state =
        mutableStateOf<WebViewPageState>(WebViewPageState(webView = webView,
            source = extensions.mappingSourceNameToSource(0)))
    val state: State<WebViewPageState> = _state

    init {
        val sourceId = savedStateHandle.get<Long>(NavigationArgs.sourceId.name)
        val bookId = savedStateHandle.get<Long>(NavigationArgs.bookId.name)
        val chapterId = savedStateHandle.get<Long>(NavigationArgs.chapterId.name)
        val url = URLDecoder.decode(savedStateHandle.get<String>(NavigationArgs.url.name),
            StandardCharsets.UTF_8.name())
        val fetcher = savedStateHandle.get<Int>(NavigationArgs.fetchType.name)

        if (sourceId != null && chapterId != null && bookId != null) {
            _state.value = state.value.copy(source = extensions.mappingSourceNameToSource(sourceId))
            _state.value = state.value.copy(book = state.value.book.copy(id = bookId))
            _state.value =
                state.value.copy(chapter = state.value.chapter?.copy(id = chapterId))
        }
        if (fetcher != null) {
            _state.value = state.value.copy(fetcher = mapFetcher(fetcher))
        }
        _state.value = state.value.copy(url = url)

        if (bookId != null && bookId != Constants.NULL_VALUE) {
            getLocalChaptersByBookName(bookId)
            getBookById(bookId = bookId)
        }
        if (bookId != null && chapterId != null && bookId != Constants.NULL_VALUE && chapterId != Constants.NULL_VALUE) {
            getLocalChapterById(chapterId)
        }

    }

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()


    @ExperimentalCoroutinesApi
    fun getInfo() {
        viewModelScope.launch {
            _eventFlow.emit(UiEvent.ShowSnackbar(
                uiText = UiText.StringResource(R.string.trying_to_fetch)
            ))
            fetcherUseCase.fetchBookDetailAndChapterDetailFromWebView(
                localBook = state.value.book,
                localChapters = state.value.chapters,
                source = state.value.source,
                insertUseCases = insetUseCases,
                deleteUseCase = deleteUseCase,
                pageSource = webView.getHtml()
            ).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        if (result.data != null) {
                            _eventFlow.emit(UiEvent.ShowSnackbar(
                                uiText = result.data
                            ))
                        }
                    }
                    is Resource.Error -> {
                        _eventFlow.emit(UiEvent.ShowSnackbar(

                            uiText = result.uiText ?: UiText.StringResource(R.string.error_unknown)
                        ))
                    }
                }
            }
        }


    }


    private fun getLocalChaptersByBookName(bookId: Long) {
        getChapterUseCase.getChaptersByBookId(bookId = bookId)
            .onEach { chapters ->
                if (chapters.isNotEmpty()) {
                    _state.value = state.value.copy(
                        chapters = chapters,
                    )
                }
            }.launchIn(viewModelScope)
    }

    private fun getBookById(bookId: Long) {
        getBookUseCases.getBookById(id = bookId).onEach { book ->
            if (book != null) {
                _state.value = state.value.copy(
                    book = book,
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun getLocalChapterById(chapterId: Long) {
        viewModelScope.launch {
            getChapterUseCase.getOneChapterById(chapterId).first { result ->
                if (result != null) {
                    _state.value = state.value.copy(
                        chapter = result,
                    )
                    //insertChaptersToLocal(state.value.chapters)
                }
                true
            }
        }
    }

    fun insertBookDetailToLocal(book: Book) {
        viewModelScope.launch(Dispatchers.IO) {
            insetUseCases.insertBook(book)
        }
    }

}

data class WebViewPageState(
    val webView: WebView,
    val url: String = "",
    val book: Book = Book(title = "", sourceId = 0L, link = ""),
    val books: List<Book> = emptyList<Book>(),
    val chapters: List<Chapter> = emptyList<Chapter>(),
    val chapter: Chapter? = null,
    val fetcher: FetchType = FetchType.LatestFetchType,
    val source: Source,
)