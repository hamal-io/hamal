package io.hamal.lib.kua.plugin.extension

import io.hamal.lib.kua.plugin.Plugin
import io.hamal.lib.kua.plugin.PluginFactory

interface ExtensionFactory : PluginFactory<Extension>

class Extension(
    override val name: String,
    override val factoryCode: String = loadFactoryCodeFromResources(name),
    val config: ExtensionConfig = ExtensionConfig(mutableMapOf()),
) : Plugin {

    companion object {
        @JvmStatic
        private fun loadFactoryCodeFromResources(extensionName: String): String { // FIXME extension name VO
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


