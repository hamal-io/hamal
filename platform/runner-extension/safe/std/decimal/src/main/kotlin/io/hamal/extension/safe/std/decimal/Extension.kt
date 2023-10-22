package io.hamal.extension.safe.std.decimal

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extension.safe.RunnerSafeExtension
import io.hamal.lib.kua.extension.safe.RunnerSafeExtensionFactory


object DecimalSafeFactory : RunnerSafeExtensionFactory {
    override fun create(sandbox: Sandbox): RunnerSafeExtension {
        return RunnerSafeExtension(name = "decimal")
    }
}

