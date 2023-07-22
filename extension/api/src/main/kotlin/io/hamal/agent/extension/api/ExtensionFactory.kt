package io.hamal.agent.extension.api

import io.hamal.lib.kua.Extension

interface ExtensionFactory {
    fun create(): Extension
}

