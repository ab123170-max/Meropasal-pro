package com.example.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.local.dao.AppSettingsDao
import com.example.data.local.entity.AppSettingsEntity

@Database(
  entities = [AppSettingsEntity::class],
  version = DatabaseMigrations.CURRENT_SCHEMA_VERSION,
  exportSchema = true,
)
@TypeConverters(DatabaseConverters::class)
abstract class MeethoPasalDatabase : RoomDatabase() {
  abstract fun appSettingsDao(): AppSettingsDao
}
