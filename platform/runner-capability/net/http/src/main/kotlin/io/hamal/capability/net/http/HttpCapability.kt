package io.hamal.capability.net.http

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.capability.Capability
import io.hamal.lib.kua.capability.CapabilityConfig
import io.hamal.lib.kua.capability.CapabilityFactory

class HttpCapabilityFactory(
    val config: CapabilityConfig = CapabilityConfig(mutableMapOf())
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