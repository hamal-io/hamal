package io.hamal.agent.extension.api

import io.hamal.lib.kua.value.ExtensionValue


interface DepExtension {
    fun create(): ExtensionValue
}

