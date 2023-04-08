package io.hamal.worker.extension.api

class StartExtensionFactory : WorkerExtensionFactory {
    override fun load(): List<WorkerExtension> {
        return listOf(StartExtension())
    }
}