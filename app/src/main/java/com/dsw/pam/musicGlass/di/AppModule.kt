package com.dsw.pam.musicGlass.di

import com.dsw.pam.musicGlass.spotify.SpotifyRepository
import com.dsw.pam.musicGlass.spotify.api.SpotifyService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideSpotifyService(): SpotifyService {
        return SpotifyService.create()
    }

    @Provides
    @Singleton
    fun provideSpotifyRepository(spotifyService: SpotifyService): SpotifyRepository {
        return SpotifyRepository(spotifyService)
    }
} 