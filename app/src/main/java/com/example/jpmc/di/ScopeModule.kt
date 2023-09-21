package com.example.jpmc.di

import com.example.jpmc.annotations.DefaultDispatcher
import com.example.jpmc.annotations.DefaultScope
import com.example.jpmc.annotations.IoDispatcher
import com.example.jpmc.annotations.IoScope
import com.example.jpmc.annotations.MainDispatcher
import com.example.jpmc.annotations.MainScope
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

@Module
@InstallIn(SingletonComponent::class)
object ScopeModule {

    @DefaultScope
    @Provides
    fun providesDefaultScope(
        @DefaultDispatcher dispatcher: CoroutineDispatcher
    ): CoroutineScope {
        return CoroutineScope(SupervisorJob() + dispatcher)
    }

    @IoScope
    @Provides
    fun providesIoScope(
        @IoDispatcher dispatcher: CoroutineDispatcher
    ): CoroutineScope {
        return CoroutineScope(SupervisorJob() + dispatcher)
    }

    @MainScope
    @Provides
    fun providesMainScope(
        @MainDispatcher dispatcher: CoroutineDispatcher
    ): CoroutineScope {
        return CoroutineScope(SupervisorJob() + dispatcher)
    }
}