package io.hamal.lib.kua.plugin

import io.hamal.lib.kua.Sandbox


interface Plugin {
    val name: String
    val factoryCode: String
}

interface PluginFactory<PLUG_IN : Plugin> {
    fun create(sandbox: Sandbox): PLUG_IN
}