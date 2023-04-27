package io.hamal.worker.extension.api

interface WorkerExtension {

    fun functionFactories(): List<WorkerExtensionFunction.Factory>

}

