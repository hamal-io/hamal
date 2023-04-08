package io.hamal.worker.extension.api

class StartExtension : WorkerExtension {
    override fun invoke() {
        println("Hello World")
    }
}