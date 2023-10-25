package io.hamal.plugin.std.debug

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extension.plugin.RunnerPluginExtension
import io.hamal.lib.kua.extension.plugin.RunnerPluginExtensionFactory


class DebugPluginFactory : RunnerPluginExtensionFactory {
    override fun create(sandbox: Sandbox): RunnerPluginExtension {
        return RunnerPluginExtension(
            name = "debug",
            internals = mapOf(
                "sleep" to SleepFunction,
            )
        )
    }
}