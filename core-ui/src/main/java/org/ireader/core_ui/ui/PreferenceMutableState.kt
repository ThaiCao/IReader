package org.ireader.core_ui.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.ireader.core_api.log.Log
import org.ireader.core_api.prefs.Preference

class PreferenceMutableState<T>(
    private val preference: Preference<T>,
    scope: CoroutineScope,
) : MutableState<T> {

    private val state = mutableStateOf(preference.defaultValue())

    override var value: T
        get() = state.value
        set(value) {
            preference.set(value)
        }

    override fun component1(): T {
        return state.value
    }

    override fun component2(): (T) -> Unit {
        return { preference.set(it) }
    }

    init {
        var isTurnOn = false
        var tries = 0
        val maxTries = 3
        while (isTurnOn && tries < maxTries) {
            kotlin.runCatching {
                scope.launch {
                    preference.changes().collect { value ->
                        try {
                            state.value = value
                            isTurnOn = true
                        } catch (e: Throwable) {
                            delay(1000L)
                            tries++
                            Log.error(e, "Failed to set Preferences")
                        }
                    }
                }

            }
        }
    }
}

fun <T> Preference<T>.asStateIn(scope: CoroutineScope): PreferenceMutableState<T> {
    return PreferenceMutableState(this, scope)
}
