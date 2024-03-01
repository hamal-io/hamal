package io.hamal.plugin.std.debug

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.extend.plugin.RunnerPluginFactory
import io.hamal.lib.kua.type.KuaString


class PluginDebugFactory : RunnerPluginFactory {
    override fun create(sandbox: Sandbox): RunnerPlugin {
        return RunnerPlugin(
            name = "debug",
            internals = mapOf(
                KuaString("sleep") to SleepFunction,
            )
        )
    }
}