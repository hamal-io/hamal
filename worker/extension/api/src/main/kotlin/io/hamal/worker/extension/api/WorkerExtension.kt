package io.hamal.worker.extension.api

import io.hamal.lib.script.api.natives.NativeFunction

interface WorkerExtension {
    fun functionFactories(): List<NativeFunction>
}

