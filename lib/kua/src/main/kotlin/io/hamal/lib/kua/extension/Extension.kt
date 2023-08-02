package io.hamal.lib.kua.extension

import io.hamal.lib.kua.value.Value


sealed interface Extension {
    val name: String // FIXME VO
    val config: ExtensionConfig
}

interface NativeExtensionFactory {
    fun create(): NativeExtension
}

class NativeExtension(
    override val name: String,
    val values: Map<String, Value>,
    override val config: ExtensionConfig = ExtensionConfig(mutableMapOf()),
) : Extension


class ScriptExtension(
    override val name: String,
    val init: String,
    val internals: Map<String, Value>,
    override val config: ExtensionConfig = ExtensionConfig(mutableMapOf()),
) : Extension {

    fun getConfigFunction() = ExtensionGetConfigFunction(config)

    fun updateConfigFunction() = ExtensionUpdateConfigFunction(config)
}


