package io.hamal.agent.extension.api

import io.hamal.lib.kua.Extension


interface DepExtension {
    fun create(): Extension
}

