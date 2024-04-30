package io.hamal.plugin.net.smtp

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.extend.plugin.RunnerPluginFactory
import io.hamal.lib.value.ValueString

class PluginSmtpFactory(
    private val sender: Sender = SenderDefaultImpl
) : RunnerPluginFactory {
    override fun create(sandbox: Sandbox): RunnerPlugin {
        return RunnerPlugin(
            name = ValueString("net.smtp"),
            internals = mapOf(
                ValueString("send") to SmtpSendFunction(sender)
            )
        )
    }
}