package io.hamal.plugin.std.debug

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.extend.plugin.RunnerPluginFactory
import io.hamal.lib.value.ValueString


class PluginDebugFactory : RunnerPluginFactory {
    override fun create(sandbox: Sandbox): RunnerPlugin {
        return RunnerPlugin(
            name = ValueString("std.debug"),
            internals = mapOf(
                ValueString("sleep") to SleepFunction,
            )
        )
    }
}