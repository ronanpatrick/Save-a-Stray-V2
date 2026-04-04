package com.example.save_a_strayv2.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import com.example.save_a_strayv2.repository.PetRepository
import com.example.save_a_strayv2.repository.PetRepositoryImpl
import javax.inject.Singleton
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.converter.kotlinx.serialization.asConverterFactory

import com.example.save_a_strayv2.BuildConfig

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_KEY
        ) {
            install(Auth)
            install(Postgrest)
            install(Storage)
        }
    }

    @Provides
    @Singleton
    fun providePetRepository(
        supabase: SupabaseClient,
        @ApplicationContext context: Context
    ): PetRepository {
        return PetRepositoryImpl(supabase, context)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        supabase: SupabaseClient
    ): com.example.save_a_strayv2.repository.AuthRepository {
        return com.example.save_a_strayv2.repository.AuthRepository(supabase)
    }

    @Provides
    @Singleton
    fun providePhLocationApi(): com.example.save_a_strayv2.network.PhLocationApi {
        val json = kotlinx.serialization.json.Json {
            ignoreUnknownKeys = true
        }
        val contentType = "application/json".toMediaType()
        @Suppress("OPT_IN_USAGE")
        return retrofit2.Retrofit.Builder()
            .baseUrl("https://psgc.gitlab.io/api/")
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(com.example.save_a_strayv2.network.PhLocationApi::class.java)
    }
}
