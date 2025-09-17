package dev.stock.dysnomia.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.IOException
import javax.inject.Singleton

@Singleton
class PreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val NAME = stringPreferencesKey("name")
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    }

    val name: Flow<String> = dataStore.data
        .catch {
            if (it is IOException) {
                Timber.e(it, "Error reading user preferences.")
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[NAME] ?: ""
        }

    val accessToken: Flow<String> = dataStore.data
        .catch {
            if (it is IOException) {
                Timber.e(it, "Error reading user preferences.")
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[ACCESS_TOKEN] ?: ""
        }

    val refreshToken: Flow<String> = dataStore.data
        .catch {
            if (it is IOException) {
                Timber.e(it, "Error reading user preferences.")
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[REFRESH_TOKEN] ?: ""
        }

    suspend fun saveAccount(name: String, accessToken: String, refreshToken: String) {
        dataStore.edit { preferences ->
            preferences[NAME] = name
            preferences[ACCESS_TOKEN] = accessToken
            preferences[REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun clearAccount() {
        dataStore.edit { preferences ->
            preferences.remove(NAME)
            preferences.remove(ACCESS_TOKEN)
            preferences.remove(REFRESH_TOKEN)
        }
    }
}
