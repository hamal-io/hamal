package io.hamal.lib.kua.extension

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.type.Type

sealed interface ExtensionFactory<EXTENSION : Extension> {
    fun create(sandbox: Sandbox): EXTENSION
}

sealed interface Extension {
    val name: String // FIXME VO
    val config: ExtensionConfig
}

interface NativeExtensionFactory : ExtensionFactory<NativeExtension>

class NativeExtension(
    override val name: String,
    val values: Map<String, Type>,
    override val config: ExtensionConfig = ExtensionConfig(mutableMapOf()),
) : Extension


interface ScriptExtensionFactory : ExtensionFactory<ScriptExtension>

class ScriptExtension(
    override val name: String,
    val init: String = loadInitFromResources(name),
    val internals: Map<String, Type>,
    override val config: ExtensionConfig = ExtensionConfig(mutableMapOf()),
) : Extension {

    companion object {
        @JvmStatic
        private fun loadInitFromResources(extensionName: String): String { // FIXME extension name VO
            val path = "${extensionName.replace(".", "/")}/extension.lua"
            val classLoader = this::class.java.classLoader
            val resource = classLoader.getResource(path)
            checkNotNull(resource) { "Unable to load: $path" }
            return String(resource.readBytes())
        }
    }

    fun getConfigFunction() = ExtensionGetConfigFunction(config)

    fun updateConfigFunction() = ExtensionUpdateConfigFunction(config)
}


