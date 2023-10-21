package io.hamal.lib.kua.plugin.capability

import io.hamal.lib.kua.plugin.Plugin
import io.hamal.lib.kua.plugin.PluginFactory
import io.hamal.lib.kua.type.Type

interface CapabilityFactory : PluginFactory<Capability>

class Capability(
    override val name: String,
    override val factoryCode: String = loadFactoryCodeFromResources(name),
    val internals: Map<String, Type> = mapOf(),
    val config: CapabilityConfig = CapabilityConfig(mutableMapOf()),
) : Plugin {

    companion object {
        @JvmStatic
        private fun loadFactoryCodeFromResources(extensionName: String): String { // FIXME capability name VO
            val path = "${extensionName.replace(".", "/")}/capability.lua"
            val classLoader = this::class.java.classLoader
            val resource = classLoader.getResource(path)
            checkNotNull(resource) { "Unable to load: $path" }
            return String(resource.readBytes())
        }
    }

    fun getConfigFunction() = CapabilityGetConfigFunction(config)

    fun updateConfigFunction() = CapabilityUpdateConfigFunction(config)
}


