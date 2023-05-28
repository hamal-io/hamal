package io.hamal.agent.extension.api

import io.hamal.lib.script.api.value.DepEnvironmentValue

interface Extension {
    fun create(): DepEnvironmentValue
}

