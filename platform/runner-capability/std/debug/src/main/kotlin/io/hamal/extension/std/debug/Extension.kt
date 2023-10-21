package io.hamal.extension.std.debug

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.capability.Capability
import io.hamal.lib.kua.capability.CapabilityFactory


class DebugExtensionFactory : CapabilityFactory {
    override fun create(sandbox: Sandbox): Capability {
        return Capability(
            name = "debug",
            internals = mapOf(
                "sleep" to SleepFunction,
            )
        )
    }
}