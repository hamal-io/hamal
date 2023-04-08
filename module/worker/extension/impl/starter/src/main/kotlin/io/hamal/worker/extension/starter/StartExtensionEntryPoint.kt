package io.hamal.worker.extension.starter

import io.hamal.worker.extension.api.WorkerExtensionEntryPoint
import io.hamal.worker.extension.api.WorkerExtensionFactory

class StartExtensionEntryPoint : WorkerExtensionEntryPoint {
    override fun factories(): List<WorkerExtensionFactory> {
        return listOf(StartExtensionFactory())
    }
}