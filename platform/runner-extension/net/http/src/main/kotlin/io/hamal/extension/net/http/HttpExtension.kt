package io.hamal.extension.net.http

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extension.ExtensionConfig
import io.hamal.lib.kua.extension.BundleExtension
import io.hamal.lib.kua.extension.BundleExtensionFactory

class HttpExtensionFactory(
    val config: ExtensionConfig = ExtensionConfig(mutableMapOf())
) : BundleExtensionFactory {


    override fun create(sandbox: Sandbox): BundleExtension {
        return BundleExtension(
            name = "net.http",
            config = config,
            internals = mapOf(
                "execute" to HttpExecuteFunction()
            )
        )
    }
}