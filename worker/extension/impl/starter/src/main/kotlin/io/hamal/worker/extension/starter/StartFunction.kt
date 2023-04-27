package io.hamal.worker.extension.starter

import io.hamal.worker.extension.api.WorkerExtensionFunction

class StartFunction : WorkerExtensionFunction {
    override fun invoke() {
        println("Hello World from Starter")
    }
}