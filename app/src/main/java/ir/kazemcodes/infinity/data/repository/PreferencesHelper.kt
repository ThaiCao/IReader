package ir.kazemcodes.infinity.data.repository

import android.content.SharedPreferences
import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class PreferencesHelper @Inject constructor(
    prefs : SharedPreferences
){
    companion object PreferenceKeys {
        const val SAVED_FONT_SIZE_PREFERENCES = "reader_font_size"
        const val SAVED_FONT_PREFERENCES = "reader_font_family"
        const val SAVED_BRIGHTNESS_PREFERENCES = "reader_brightness"

        const val SAVED_LIBRARY_LAYOUT_KEY = "library_layout_type"
        const val SAVED_BROWSE_LAYOUT_KEY = "browser_layout_type"

        /** Setting Pref**/
        const val SAVED_DOH_KEY = "SAVED_DOH_KEY"


    }
    private val flowPrefs = FlowSharedPreferences(prefs)


    val readerFontScale = flowPrefs.getInt(SAVED_FONT_SIZE_PREFERENCES, 18)
    val readerFont = flowPrefs.getInt(SAVED_FONT_PREFERENCES, 0)
    val readerBrightness = flowPrefs.getFloat(SAVED_BRIGHTNESS_PREFERENCES, .6f)
    val libraryLayoutTypeStateKey = flowPrefs.getInt(SAVED_LIBRARY_LAYOUT_KEY, 0)
    val browseLayoutTypeStateKey = flowPrefs.getInt(SAVED_BROWSE_LAYOUT_KEY, 0)
    val dohStateKey = flowPrefs.getInt(SAVED_DOH_KEY, 0)

    fun setFontScale(fontSize : Int) {
        readerFontScale.set(fontSize)
    }
    /**
     * save the index of font according to position of font in fonts list.
     */
    fun setFont(fontIndex: Int) {
        readerFont.set(fontIndex)
    }
    fun setBrightness(brightness : Float) {
        readerBrightness.set(brightness)
    }
    fun setLibraryLayoutTypeStateUseCase(layoutIndex: Int) {
        libraryLayoutTypeStateKey.set(layoutIndex)
    }
    fun setBrowseLayoutTypeStateUseCase(layoutIndex: Int) {
        browseLayoutTypeStateKey.set(layoutIndex)
    }
    fun setDohPrefUseCase(dohPref: Int) {
        dohStateKey.set(dohPref)
    }
}