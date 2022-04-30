package org.ireader.domain.services.tts_service

import android.speech.tts.Voice
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.ireader.common_models.entities.Book
import org.ireader.common_models.entities.Chapter
import org.ireader.core_api.source.Source
import org.ireader.core_ui.theme.FontType
import java.util.Locale
import javax.inject.Inject

interface TTSState {

    var ttsIsLoading: Boolean
    var isPlaying: Boolean
    var ttsContent: State<List<String>?>?

    var currentReadingParagraph: Int
    var prevPar: Int
    var autoNextChapter: Boolean
    var languages: List<Locale>
    var voices: List<Voice>
    var currentVoice: String
    var prevVoice: String
    var currentLanguage: String
    var utteranceId: String
    var prevLanguage: String
    var pitch: Float
    var prevPitch: Float
    var speechSpeed: Float
    var prevSpeechSpeed: Float
    var ttsBook: Book?
    var ttsChapter: Chapter?
    var ttsSource: Source?
    var ttsChapters: List<Chapter>
    var ttsCurrentChapterIndex: Int
    var font: FontType
    var fontSize: Int
    var lineHeight: Int
    var isServiceConnected: Boolean


}

class TTSStateImpl @Inject constructor() : TTSState {
    override var font by mutableStateOf<FontType>(FontType.Poppins)
    override var lineHeight by mutableStateOf<Int>(25)
    override var isServiceConnected by mutableStateOf<Boolean>(false)
    override var fontSize by mutableStateOf<Int>(18)
    override var ttsIsLoading by mutableStateOf<Boolean>(false)
    override var currentReadingParagraph: Int by mutableStateOf<Int>(0)
    override var prevPar: Int by mutableStateOf<Int>(0)
    override var languages by mutableStateOf<List<Locale>>(emptyList())
    override var voices by mutableStateOf<List<Voice>>(emptyList())

    override var currentVoice by mutableStateOf<String>("")
    override var prevVoice by mutableStateOf<String>("")
    override var currentLanguage by mutableStateOf<String>("")
    override var prevLanguage by mutableStateOf<String>("")
    override var isPlaying by mutableStateOf<Boolean>(false)
    override var ttsContent: State<List<String>?>? =
        derivedStateOf { ttsChapter?.content?.filter { it.isNotBlank() }?.map { it.trim() } }
    override var autoNextChapter by mutableStateOf<Boolean>(false)
    override var pitch by mutableStateOf<Float>(.8f)
    override var prevPitch by mutableStateOf<Float>(.8f)
    override var speechSpeed by mutableStateOf<Float>(.8f)
    override var prevSpeechSpeed by mutableStateOf<Float>(.8f)
    override var ttsBook by mutableStateOf<Book?>(null)
    override var ttsSource by mutableStateOf<Source?>(null)
    override var ttsChapter by mutableStateOf<Chapter?>(null)
    override var ttsChapters by mutableStateOf<List<Chapter>>(emptyList())
    override var ttsCurrentChapterIndex by mutableStateOf<Int>(-1)

    override var utteranceId by mutableStateOf<String>("")
}
