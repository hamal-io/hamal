package io.hamal.extension.net.http

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.capability.ExtensionConfig
import io.hamal.lib.kua.capability.Capability
import io.hamal.lib.kua.capability.CapabilityFactory

class HttpExtensionFactory(
    val config: ExtensionConfig = ExtensionConfig(mutableMapOf())
) : CapabilityFactory {


    override fun create(sandbox: Sandbox): Capability {
        return Capability(
            name = "net.http",
            config = config,
            internals = mapOf(
                "execute" to HttpExecuteFunction()
            )
        )
    }
}