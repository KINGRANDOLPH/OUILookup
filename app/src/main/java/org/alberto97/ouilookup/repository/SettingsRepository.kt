package org.alberto97.ouilookup.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

interface ISettingsRepository {
    fun getLastDbUpdate(): Flow<Long>
    suspend fun setLastDbUpdate(value: Long)
}

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
): ISettingsRepository {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "oui_settings")
    private val lastDbUpdateKey = longPreferencesKey("last_db_update")

    override fun getLastDbUpdate(): Flow<Long> {
        return context.dataStore.data
            .map { settings -> settings[lastDbUpdateKey] ?: 0 }
    }

    override suspend fun setLastDbUpdate(value: Long) {
        context.dataStore.edit { settings ->
            settings[lastDbUpdateKey] = value
        }
    }
}