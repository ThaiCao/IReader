package ir.kazemcodes.infinity.di

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ir.kazemcodes.infinity.core.data.local.BookDatabase
import ir.kazemcodes.infinity.core.data.local.dao.LibraryBookDao
import ir.kazemcodes.infinity.core.data.local.dao.LibraryChapterDao
import ir.kazemcodes.infinity.core.data.repository.*
import ir.kazemcodes.infinity.core.domain.repository.LocalBookRepository
import ir.kazemcodes.infinity.core.domain.repository.LocalChapterRepository
import ir.kazemcodes.infinity.core.domain.repository.RemoteRepository
import ir.kazemcodes.infinity.core.domain.repository.Repository
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModule {


    @Provides
    @Singleton
    fun providesRepository(
        localChapterRepository: LocalChapterRepository,
        localBookRepository: LocalBookRepository,
        remoteRepository: RemoteRepository,
        preferencesHelper: PreferencesHelper,
        bookDatabase: BookDatabase,
    ): Repository {
        return RepositoryImpl(
            localChapterRepository = localChapterRepository,
            localBookRepository = localBookRepository,
            remoteRepository = remoteRepository,
            preferencesHelper = preferencesHelper,
            database = bookDatabase
        )
    }

    @Provides
    @Singleton
    fun providesLocalChapterRepository(libraryChapterDao: LibraryChapterDao): LocalChapterRepository {
        return LocalChapterRepositoryImpl(libraryChapterDao)
    }


    @Provides
    @Singleton
    fun providesLibraryRepository(
        libraryBookDao: LibraryBookDao,
        libraryChapterDao: LibraryChapterDao,
        database: BookDatabase,
    ): LocalBookRepository {
        return LocalBookRepositoryImpl(libraryBookDao, libraryChapterDao,database)
    }

    @Provides
    @Singleton
    fun providesRemoteBookRepository(
        libraryBookDao: LibraryBookDao,
        localBookRepository: LocalBookRepository,
        database: BookDatabase,
    ): RemoteRepository {
        return RemoteRepositoryImpl(libraryBookDao, localBookRepository, database)
    }

    @Provides
    @Singleton
    fun providesContext(@ApplicationContext appContext: Context): Context {
        return appContext
    }

    @Singleton
    @Provides
    fun providesMosh(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }
}
