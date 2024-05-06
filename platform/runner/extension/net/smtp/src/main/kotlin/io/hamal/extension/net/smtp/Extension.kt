package io.hamal.extension.net.smtp

import io.hamal.extension.std.`throw`.ExtensionStdThrowFactory
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extend.extension.RunnerExtension
import io.hamal.lib.kua.extend.extension.RunnerExtensionFactory

object ExtensionNetSmtpFactory : RunnerExtensionFactory {
    override fun create(sandbox: Sandbox): RunnerExtension {
        return RunnerExtension(
            name = ValueString("net.smtp"),
            dependencies = listOf(ExtensionStdThrowFactory)
        )
    }
}