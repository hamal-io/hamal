package io.hamal.plugin.net.smtp

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.extend.plugin.RunnerPluginFactory
import io.hamal.lib.kua.type.KuaString

class PluginSmtpFactory(
    private val sender: Sender = SenderDefaultImpl
) : RunnerPluginFactory {
    override fun create(sandbox: Sandbox): RunnerPlugin {
        return RunnerPlugin(
            name = KuaString("net.smtp"),
            internals = mapOf(
                KuaString("send") to SmtpSendFunction(sender)
            )
        )
    }
}