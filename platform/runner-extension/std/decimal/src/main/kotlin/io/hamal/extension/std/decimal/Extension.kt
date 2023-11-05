package io.hamal.extension.std.decimal

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extension.script.RunnerScriptExtension
import io.hamal.lib.kua.extension.script.RunnerScriptExtensionFactory


object DecimalScriptFactory : RunnerScriptExtensionFactory {
    override fun create(sandbox: Sandbox): RunnerScriptExtension {
        return RunnerScriptExtension(name = "decimal")
    }
}

