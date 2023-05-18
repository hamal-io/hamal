package io.hamal.agent.extension.api

import io.hamal.lib.script.api.Environment

interface Extension {
    fun create(): Environment
}

