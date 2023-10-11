package io.hamal.extension.std.debug

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extension.BundleExtension
import io.hamal.lib.kua.extension.BundleExtensionFactory


class DebugExtensionFactory : BundleExtensionFactory {
    override fun create(sandbox: Sandbox): BundleExtension {
        return BundleExtension(
            name = "debug",
            internals = mapOf(
                "sleep" to SleepFunction,
            )
        )
    }
}