package com.testgrader

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
interface AppModule {
    @IntoSet
    @Binds
    fun bindTimberInitializer(timberInitializer: TimberInitializer): Initializer

    @IntoSet
    @Binds
    fun bindOpenCvInitializer(openCvInitializer: OpenCvInitializer): Initializer
}