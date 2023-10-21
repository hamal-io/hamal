package io.hamal.lib.kua.capability

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.type.Type

interface CapabilityFactory {
    fun create(sandbox: Sandbox): Capability
}

class Capability(
    val name: String,
    val internals: Map<String, Type>,
    val factoryCode: String = loadFactoryCodeFromResources(name),
    val config: CapabilityConfig = CapabilityConfig(mutableMapOf()),
) {

    companion object {
        @JvmStatic
        private fun loadFactoryCodeFromResources(extensionName: String): String { // FIXME extension name VO
            val path = "${extensionName.replace(".", "/")}/capability.lua"
            val classLoader = this::class.java.classLoader
            val resource = classLoader.getResource(path)
            checkNotNull(resource) { "Unable to load: $path" }
            return String(resource.readBytes())
        }
    }

    fun getConfigFunction() = ExtensionGetConfigFunction(config)

    fun updateConfigFunction() = ExtensionUpdateConfigFunction(config)
}


