package io.hamal.worker.extension.starter

import io.hamal.worker.extension.api.WorkerExtension

class StartExtension : WorkerExtension {
    override fun invoke() {
        println("Hello World from Starter")
    }
}