package dev.stock.dysnomia.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import dev.stock.dysnomia.model.AuthTokens
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

interface PreferencesRepository {
    suspend fun saveAccount(name: String, accessToken: String, refreshToken: String)
    suspend fun saveTokens(authTokens: AuthTokens)
    suspend fun clearAccount()
    suspend fun setNotFirstLaunch()
    val name: Flow<String>
    val accessToken: Flow<String>
    val refreshToken: Flow<String>
    val firstLaunch: Flow<Boolean>
}

@Singleton
class PreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : PreferencesRepository {
    private companion object {
        val NAME = stringPreferencesKey("name")
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val FIRST_LAUNCH = booleanPreferencesKey("first_launch")
    }

    override val name: Flow<String> = dataStore.data
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

    override val accessToken: Flow<String> = dataStore.data
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

    override val refreshToken: Flow<String> = dataStore.data
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

    override val firstLaunch: Flow<Boolean> = dataStore.data
        .catch {
            if (it is IOException) {
                Timber.e(it, "Error reading user preferences.")
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[FIRST_LAUNCH] ?: true
        }

    override suspend fun saveAccount(name: String, accessToken: String, refreshToken: String) {
        dataStore.edit { preferences ->
            preferences[NAME] = name
            preferences[ACCESS_TOKEN] = accessToken
            preferences[REFRESH_TOKEN] = refreshToken
        }
    }

    override suspend fun saveTokens(authTokens: AuthTokens) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = authTokens.accessToken
            preferences[REFRESH_TOKEN] = authTokens.refreshToken
        }
    }

    override suspend fun clearAccount() {
        dataStore.edit { preferences ->
            preferences.remove(NAME)
            preferences.remove(ACCESS_TOKEN)
            preferences.remove(REFRESH_TOKEN)
        }
    }

    override suspend fun setNotFirstLaunch() {
        dataStore.edit { preferences ->
            preferences[FIRST_LAUNCH] = false
        }
    }
}
