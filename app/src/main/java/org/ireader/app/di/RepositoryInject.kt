package org.ireader.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.ireader.core_catalogs.service.CatalogRemoteRepository
import org.ireader.data.local.dao.CatalogDao
import org.ireader.data.local.dao.DownloadDao
import org.ireader.data.local.dao.HistoryDao
import org.ireader.data.local.dao.LibraryBookDao
import org.ireader.data.local.dao.RemoteKeysDao
import org.ireader.data.local.dao.UpdatesDao
import org.ireader.data.local.dao.ChapterDao
import org.ireader.data.repository.CatalogRemoteRepositoryImpl
import org.ireader.data.repository.DownloadRepositoryImpl
import org.ireader.data.repository.HistoryRepositoryImpl
import org.ireader.data.repository.LocalBookRepositoryImpl
import org.ireader.data.repository.LocalChapterRepositoryImpl
import org.ireader.data.repository.RemoteKeyRepositoryImpl
import org.ireader.data.repository.UpdatesRepositoryImpl
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RepositoryInject {

    @Provides
    @Singleton
    fun provideRemoteKeyRepository(
        remoteKeysDao: RemoteKeysDao,
    ): org.ireader.common_data.repository.RemoteKeyRepository {
        return RemoteKeyRepositoryImpl(
            dao = remoteKeysDao,
        )
    }

    @Singleton
    @Provides
    fun provideDownloadRepository(
        downloadDao: DownloadDao,
    ): org.ireader.common_data.repository.DownloadRepository {
        return DownloadRepositoryImpl(downloadDao)
    }

    @Singleton
    @Provides
    fun provideUpdatesRepository(
        updatesDao: UpdatesDao,
    ): org.ireader.common_data.repository.UpdatesRepository {
        return UpdatesRepositoryImpl(updatesDao)
    }

    @Provides
    @Singleton
    fun provideCatalogRemoteRepository(catalogDao: CatalogDao): CatalogRemoteRepository {
        return CatalogRemoteRepositoryImpl(dao = catalogDao)
    }

    @Provides
    @Singleton
    fun providesLocalChapterRepository(ChapterDao: ChapterDao): org.ireader.common_data.repository.LocalChapterRepository {
        return LocalChapterRepositoryImpl(ChapterDao)
    }

    @Provides
    @Singleton
    fun providesLibraryRepository(
        libraryBookDao: LibraryBookDao,
        remoteKeysDao: RemoteKeysDao,
    ): org.ireader.common_data.repository.LocalBookRepository {
        return LocalBookRepositoryImpl(
            libraryBookDao,
            remoteKeysDao = remoteKeysDao
        )
    }

    @Provides
    @Singleton
    fun providesHistoryRepository(
        historyDao: HistoryDao,
    ): org.ireader.common_data.repository.HistoryRepository {
        return HistoryRepositoryImpl(historyDao)
    }
}