package io.hamal.worker.extension.api

import io.hamal.lib.script.api.Environment
import io.hamal.lib.script.api.native_.NativeFunction

interface WorkerExtension {
    fun functionFactories(): List<NativeFunction>

    fun environments(): List<Environment>
}

