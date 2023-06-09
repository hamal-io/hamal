package io.hamal.agent.extension.api

import io.hamal.lib.common.value.EnvValue


interface Extension {
    fun create(): EnvValue
}

