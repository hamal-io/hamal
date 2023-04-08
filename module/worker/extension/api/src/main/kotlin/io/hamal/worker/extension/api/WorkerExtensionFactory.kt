package io.hamal.worker.extension.api

interface WorkerExtensionFactory {

    fun load(): List<WorkerExtension>

}