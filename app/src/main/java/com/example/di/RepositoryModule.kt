package com.example.di

import com.example.core.dispatcher.DefaultDispatcherProvider
import com.example.core.dispatcher.DispatcherProvider
import com.example.data.remote.NoOpRemoteDataSource
import com.example.data.remote.RemoteDataSource
import com.example.data.repository.SettingsRepositoryImpl
import com.example.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

  @Binds
  @Singleton
  abstract fun bindDispatcherProvider(
    impl: DefaultDispatcherProvider,
  ): DispatcherProvider

  @Binds
  @Singleton
  abstract fun bindRemoteDataSource(
    impl: NoOpRemoteDataSource,
  ): RemoteDataSource

  @Binds
  @Singleton
  abstract fun bindSettingsRepository(
    impl: SettingsRepositoryImpl,
  ): SettingsRepository
}
