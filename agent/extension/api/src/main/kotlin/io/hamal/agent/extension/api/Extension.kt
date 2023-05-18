package io.hamal.agent.extension.api

import io.hamal.lib.script.api.Environment
import io.hamal.lib.script.api.native_.FunctionValue

interface Extension {
    fun functionFactories(): List<FunctionValue>

    fun environments(): List<Environment>
}

