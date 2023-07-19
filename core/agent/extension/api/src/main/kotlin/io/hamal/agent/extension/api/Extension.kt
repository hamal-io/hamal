package io.hamal.agent.extension.api

import io.hamal.lib.kua.value.ModuleValue


interface Extension {
    fun create(): ModuleValue
}

