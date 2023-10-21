package io.hamal.extension.unsafe.std.debug

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extension.unsafe.RunnerUnsafeExtension
import io.hamal.lib.kua.extension.unsafe.RunnerUnsafeExtensionFactory


class DebugExtensionFactory : RunnerUnsafeExtensionFactory {
    override fun create(sandbox: Sandbox): RunnerUnsafeExtension {
        return RunnerUnsafeExtension(
            name = "debug",
            internals = mapOf(
                "sleep" to SleepFunction,
            )
        )
    }
}