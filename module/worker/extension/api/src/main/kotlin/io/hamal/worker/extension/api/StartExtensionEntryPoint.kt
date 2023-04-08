package io.hamal.worker.extension.api

class StartExtensionEntryPoint : WorkerExtensionEntryPoint {
    override fun factories(): List<WorkerExtensionFactory> {
        return listOf(StartExtensionFactory())
    }
}