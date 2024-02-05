package org.geo.gitnetwork.model

import java.util.concurrent.atomic.AtomicBoolean

class ServiceState {
    private val isRunning = AtomicBoolean(false)
    private val isLoading = AtomicBoolean(false)

    fun active(): AtomicBoolean = isRunning
    fun loading(): AtomicBoolean = isLoading
}