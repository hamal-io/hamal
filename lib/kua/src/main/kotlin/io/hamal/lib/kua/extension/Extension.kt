package io.hamal.lib.kua.extension

import io.hamal.lib.kua.value.Value

sealed interface ExtensionFactory<EXTENSION : Extension> {
    fun create(): EXTENSION
}

sealed interface Extension {
    val name: String // FIXME VO
    val config: ExtensionConfig
}

interface NativeExtensionFactory : ExtensionFactory<NativeExtension>

class NativeExtension(
    override val name: String,
    val values: Map<String, Value>,
    override val config: ExtensionConfig = ExtensionConfig(mutableMapOf()),
) : Extension


interface ScriptExtensionFactory : ExtensionFactory<ScriptExtension>

class ScriptExtension(
    override val name: String,
    val init: String = loadInitFromResources(name),
    val internals: Map<String, Value>,
    override val config: ExtensionConfig = ExtensionConfig(mutableMapOf()),
) : Extension {

    companion object {
        @JvmStatic
        private fun loadInitFromResources(extensionName: String): String { // FIXME extension name VO
            val path = "$extensionName/extension.lua"
            val classLoader = this::class.java.classLoader
            val resource = classLoader.getResource(path)
            checkNotNull(resource) { "Unable to load: $path" }
            return String(resource.readBytes())
        }
    }

    fun getConfigFunction() = ExtensionGetConfigFunction(config)

    fun updateConfigFunction() = ExtensionUpdateConfigFunction(config)
}


