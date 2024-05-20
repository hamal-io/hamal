package io.hamal.extension.std.log

import io.hamal.lib.domain.vo.ExtensionName.Companion.ExtensionName
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extend.extension.RunnerExtension
import io.hamal.lib.kua.extend.extension.RunnerExtensionFactory

object ExtensionStdLogFactory : RunnerExtensionFactory {
    override fun create(sandbox: Sandbox): RunnerExtension {
        return RunnerExtension(name = ExtensionName("std.log"))
    }
}
