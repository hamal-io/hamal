package io.hamal.extension.std.log

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extension.BundleExtension
import io.hamal.lib.kua.extension.BundleExtensionFactory


object DecimalExtensionFactory : BundleExtensionFactory {
    override fun create(sandbox: Sandbox): BundleExtension {
        return BundleExtension(
            name = "decimal",
            internals = mapOf()
        )
    }
}

