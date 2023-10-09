package io.hamal.extension.std.debug

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extension.ScriptExtension
import io.hamal.lib.kua.extension.ScriptExtensionFactory


class DebugExtensionFactory : ScriptExtensionFactory {
    override fun create(sandbox: Sandbox): ScriptExtension {
        return ScriptExtension(
            name = "debug",
            internals = mapOf(
                "sleep" to SleepFunction,
            )
        )
    }
}