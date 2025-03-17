package dev.stock.dysnomia.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Singleton

@Singleton
class PreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val NAME = stringPreferencesKey("name")
        const val TAG = "PreferencesRepository"
    }

    val name: Flow<String> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading user preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[NAME] ?: ""
        }

    suspend fun saveName(name: String) {
        dataStore.edit { preferences ->
            preferences[NAME] = name
        }
    }

    suspend fun clearName() {
        dataStore.edit { preferences ->
            preferences.remove(NAME)
        }
    }
}
