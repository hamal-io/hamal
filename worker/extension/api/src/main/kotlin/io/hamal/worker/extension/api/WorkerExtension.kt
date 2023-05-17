package io.hamal.worker.extension.api

import io.hamal.lib.script.api.Environment
import io.hamal.lib.script.api.native_.FunctionValue

interface WorkerExtension {
    fun functionFactories(): List<FunctionValue>

    fun environments(): List<Environment>
}

