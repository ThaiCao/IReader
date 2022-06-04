package org.ireader.domain.use_cases.local.chapter_usecases

import kotlinx.datetime.Clock
import org.ireader.common_extensions.currentTimeToLong
import org.ireader.common_models.entities.Chapter
import org.ireader.common_models.entities.History
import org.ireader.core_ui.preferences.UiPreferences
import org.ireader.domain.use_cases.history.HistoryUseCase
import org.ireader.domain.use_cases.local.LocalInsertUseCases
import javax.inject.Inject

class FindChaptersByKey @Inject constructor(private val localChapterRepository: org.ireader.common_data.repository.LocalChapterRepository) {
    suspend operator fun invoke(key: String): List<Chapter> {
        return localChapterRepository.findChaptersByKey(key)
    }
}

class FindChapterByKey @Inject constructor(private val localChapterRepository: org.ireader.common_data.repository.LocalChapterRepository) {
    suspend operator fun invoke(key: String): Chapter? {
        return localChapterRepository.findChapterByKey(key)
    }
}

class UpdateLastReadTime @Inject constructor(
    private val insertUseCases: LocalInsertUseCases,
    private val historyUseCase: HistoryUseCase,
    private val uiPreferences: UiPreferences
) {
    suspend operator fun invoke(chapter: Chapter, updateDateFetched: Boolean = false) {

        if (!uiPreferences.incognitoMode().read()) {
            val history = historyUseCase.findHistory(chapter.id)
            insertUseCases.insertChapter(
                chapter = chapter.copy(
                    read = true,
                    dateFetch = if (updateDateFetched) Clock.System.now()
                        .toEpochMilliseconds() else chapter.dateFetch
                )
            )


            historyUseCase.insertHistory(
                History(
                    bookId = chapter.bookId,
                    chapterId = chapter.id,
                    readAt = currentTimeToLong(),
                    progress = history?.progress?:0
                )
            )
        }
    }
}
