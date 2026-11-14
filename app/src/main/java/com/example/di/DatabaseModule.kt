package com.example.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.AppSettingsDao
import com.example.data.local.db.DatabaseMigrations
import com.example.data.local.db.MeethoPasalDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

  @Provides
  @Singleton
  fun provideMeethoPasalDatabase(
    @ApplicationContext context: Context,
  ): MeethoPasalDatabase =
    Room.databaseBuilder(
        context,
        MeethoPasalDatabase::class.java,
        DatabaseMigrations.DATABASE_NAME,
      )
      .addMigrations(*DatabaseMigrations.ALL_MIGRATIONS)
      .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
      .build()

  @Provides
  @Singleton
  fun provideAppSettingsDao(database: MeethoPasalDatabase): AppSettingsDao =
    database.appSettingsDao()
}
