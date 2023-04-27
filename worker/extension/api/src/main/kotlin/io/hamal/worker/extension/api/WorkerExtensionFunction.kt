package io.hamal.worker.extension.api

interface WorkerExtensionFunction {

    operator fun invoke()

    interface Factory {
        operator fun invoke(): WorkerExtensionFunction
    }
}