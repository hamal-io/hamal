package io.hamal.worker.extension.api

interface WorkerExtensionEntryPoint {

    fun factories(): List<WorkerExtensionFactory>
    
}