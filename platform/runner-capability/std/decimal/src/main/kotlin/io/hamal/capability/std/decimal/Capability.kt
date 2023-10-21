package io.hamal.capability.std.log

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.plugin.capability.Capability
import io.hamal.lib.kua.plugin.capability.CapabilityFactory


object DecimalCapabilityFactory : CapabilityFactory {
    override fun create(sandbox: Sandbox): Capability {
        return Capability(
            name = "decimal",
            internals = mapOf()
        )
    }
}

