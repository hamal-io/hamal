package io.hamal.extension.web3.hml

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extension.ExtensionConfig
import io.hamal.lib.kua.extension.BundleExtension
import io.hamal.lib.kua.extension.BundleExtensionFactory
import io.hamal.lib.kua.type.StringType

class HmlExtensionFactory : BundleExtensionFactory {
    val config = ExtensionConfig(
        mutableMapOf(
            "host" to StringType("http://localhost:8000")
        )
    )

    override fun create(sandbox: Sandbox): BundleExtension {
        return BundleExtension(
            name = "web3.hml",
            config = config,
            internals = mapOf(
                "execute" to HmlExecuteFunction(config),
            )
        )
    }
}
