package org.alberto97.ouilookup.db

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.alberto97.ouilookup.Extensions.readRawTextFile
import org.alberto97.ouilookup.R
import org.alberto97.ouilookup.repository.ISettingsRepository

@Database(
    entities = [Oui::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ouiDao(): OuiDao
}

class RoomCallback(
    @ApplicationContext private val context: Context,
    private val settings: dagger.Lazy<ISettingsRepository>
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        CoroutineScope(Dispatchers.IO).launch {
            setLastUpdate()
        }
    }

    private suspend fun setLastUpdate() {
        val bundledDbMillis = context.resources.readRawTextFile(R.raw.oui_date_millis).toLong()
        settings.get().setLastDbUpdate(bundledDbMillis)
    }
}
