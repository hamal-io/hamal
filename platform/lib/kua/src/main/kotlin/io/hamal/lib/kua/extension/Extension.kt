package io.hamal.lib.kua.extension

import io.hamal.lib.kua.Sandbox


interface RunnerExtension {
    val name: String
    val factoryCode: String
}

interface RunnerExtensionFactory<EXTENSION : RunnerExtension> {
    fun create(sandbox: Sandbox): EXTENSION
}