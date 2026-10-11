package com.gymrpg

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import java.io.IOException
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

private val Context.demoStore by preferencesDataStore("m0_visual_preferences")

data class DemoSettings(
    val fullGlass: Boolean = false,
    val reducedMotion: Boolean = false,
    val systemFont: Boolean = false,
    val theme: String = "Dark",
)

class DemoPreferences internal constructor(private val store: DataStore<Preferences>) {
    constructor(context: Context) : this(context.applicationContext.demoStore)
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    private val full = booleanPreferencesKey("full_glass")
    private val reduced = booleanPreferencesKey("reduced_motion")
    private val font = booleanPreferencesKey("system_font")
    private val theme = stringPreferencesKey("theme")
    val settings = store.data.catch { error ->
        if (error is IOException) {
            _error.value = "Saved appearance settings could not be read; using defaults."
            emit(emptyPreferences())
        } else throw error
    }.map { DemoSettings(it[full] ?: false, it[reduced] ?: false, it[font] ?: false, it[theme] ?: "Dark") }
    suspend fun save(value: DemoSettings) {
        try {
            store.edit {
                it[full] = value.fullGlass
                it[reduced] = value.reducedMotion
                it[font] = value.systemFont
                it[theme] = value.theme
            }
            _error.value = null
        } catch (error: IOException) {
            _error.value = "Appearance settings could not be saved; changes may be temporary."
        }
    }
}
