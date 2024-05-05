package io.hamal.extension.std.table

import io.hamal.lib.common.value.ValueString
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extend.extension.RunnerExtension
import io.hamal.lib.kua.extend.extension.RunnerExtensionFactory


object ExtensionStdTableFactory : RunnerExtensionFactory {
    override fun create(sandbox: Sandbox): RunnerExtension {
        return RunnerExtension(name = ValueString("std.table"))
    }
}

