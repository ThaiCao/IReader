package org.ireader.domain.use_cases.remote

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import org.ireader.common_extensions.async.withIOContext
import org.ireader.common_models.entities.Book
import org.ireader.common_models.entities.Book.Companion.toBookInfo
import org.ireader.common_models.entities.Chapter
import org.ireader.common_models.entities.toChapter
import org.ireader.core_api.log.Log
import org.ireader.core_api.source.Source
import org.ireader.core_ui.exceptionHandler
import javax.inject.Inject

class GetRemoteChapters @Inject constructor(@ApplicationContext private val context: Context) {
    suspend operator fun invoke(
        book: Book,
        source: Source,
        onSuccess: suspend (List<Chapter>) -> Unit,
        onError: suspend (org.ireader.common_extensions.UiText?) -> Unit,
    ) {
        withIOContext {
            kotlin.runCatching {
                try {
                    Log.debug { "Timber: GetRemoteChaptersUseCase was Called" }

                    val chapters = source.getChapterList(manga = book.toBookInfo(source.id))

                    onSuccess(chapters.map { it.toChapter(book.id) })
                    Log.debug { "Timber: GetRemoteChaptersUseCase was Finished Successfully" }
                } catch (e: CancellationException) {

                } catch (e: Throwable) {
                    onError(exceptionHandler(e))
                }
            }.getOrElse { e ->
                onError(exceptionHandler(e))
            }
        }
    }
}
