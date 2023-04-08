package io.hamal.worker.extension.starter

import io.hamal.worker.extension.api.WorkerExtension
import io.hamal.worker.extension.api.WorkerExtensionFactory

class StartExtensionFactory : WorkerExtensionFactory {
    override fun load(): List<WorkerExtension> {
        return listOf(StartExtension())
    }
}