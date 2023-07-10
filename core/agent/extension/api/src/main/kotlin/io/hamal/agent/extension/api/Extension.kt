package io.hamal.agent.extension.api

import io.hamal.lib.script.api.value.EnvValue


interface Extension {
    fun create(): EnvValue
}

