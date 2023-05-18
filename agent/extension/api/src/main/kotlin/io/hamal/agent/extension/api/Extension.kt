package io.hamal.agent.extension.api

import io.hamal.lib.script.api.value.EnvironmentValue

interface Extension {
    fun create(): EnvironmentValue
}

