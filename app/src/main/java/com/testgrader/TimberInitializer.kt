package com.testgrader

import timber.log.Timber
import javax.inject.Inject


class TimberInitializer @Inject constructor() : Initializer {
    override fun initialize() {
        Timber.plant(Timber.DebugTree())
    }
}