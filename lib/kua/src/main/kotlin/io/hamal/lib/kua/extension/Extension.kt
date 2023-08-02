package io.hamal.lib.kua.extension

import io.hamal.lib.kua.value.Value
import java.nio.file.Path
import kotlin.io.path.Path

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
    val init: String,
    val internals: Map<String, Value>,
    override val config: ExtensionConfig = ExtensionConfig(mutableMapOf()),
) : Extension {

    companion object {
        fun loadInitFromResources(filename: String): String {
            return loadInitFromResources(Path(filename))
        }

        fun loadInitFromResources(path: Path): String {
            val classLoader = this::class.java.classLoader
            val resource = classLoader.getResource(path.toString())
            checkNotNull(resource) { "Unable to load: $path" }
            return String(resource.readBytes())
        }
    }

    fun getConfigFunction() = ExtensionGetConfigFunction(config)

    fun updateConfigFunction() = ExtensionUpdateConfigFunction(config)

}


