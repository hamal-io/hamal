package io.hamal.plugin.net.smtp

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.extend.plugin.RunnerPluginFactory
import io.hamal.plugin.net.smtp.function.SmtpSendFunction

class SmtpPluginFactory : RunnerPluginFactory {
    override fun create(sandbox: Sandbox): RunnerPlugin {
        return RunnerPlugin(
            name = "net.smtp",
            internals = mapOf(
                "send" to SmtpSendFunction()
            )
        )
    }
}