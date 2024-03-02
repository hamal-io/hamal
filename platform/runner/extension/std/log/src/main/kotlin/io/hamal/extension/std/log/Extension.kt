package io.hamal.extension.std.log

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extend.extension.RunnerExtension
import io.hamal.lib.kua.extend.extension.RunnerExtensionFactory
import io.hamal.lib.kua.type.KuaString

object ExtensionLogFactory : RunnerExtensionFactory {
    override fun create(sandbox: Sandbox): RunnerExtension {
        return RunnerExtension(name = KuaString("log"))
    }
}
