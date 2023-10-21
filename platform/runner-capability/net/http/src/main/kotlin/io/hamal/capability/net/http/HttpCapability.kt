package io.hamal.capability.net.http

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.plugin.capability.Capability
import io.hamal.lib.kua.plugin.capability.CapabilityConfig
import io.hamal.lib.kua.plugin.capability.CapabilityFactory

class HttpCapabilityFactory(
    val config: CapabilityConfig = CapabilityConfig(mutableMapOf())
) : CapabilityFactory {


    override fun create(sandbox: Sandbox): Capability {
        return Capability(
            name = "net.http",
            internals = mapOf(
                "execute" to HttpExecuteFunction()
            ),
            config = config
        )
    }
}