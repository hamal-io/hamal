package io.hamal.worker.extension.api

import io.hamal.lib.script.api.value.Value

interface WorkerExtensionFunction {
    operator fun invoke(ctx: Context) : Value

    interface Factory {
        fun create(): WorkerExtensionFunction
    }

    interface Context
}

class DefaultContext : WorkerExtensionFunction.Context