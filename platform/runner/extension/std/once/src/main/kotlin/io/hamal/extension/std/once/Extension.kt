package io.hamal.extension.std.once

import io.hamal.extension.std.table.ExtensionStdTableFactory
import io.hamal.extension.std.`throw`.ExtensionStdThrowFactory
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extend.extension.RunnerExtension
import io.hamal.lib.kua.extend.extension.RunnerExtensionFactory


object ExtensionStdOnceFactory : RunnerExtensionFactory {
    override fun create(sandbox: Sandbox): RunnerExtension {
        return RunnerExtension(
            name = ValueString("std.once"),
            dependencies = listOf(ExtensionStdThrowFactory, ExtensionStdTableFactory)
        )
    }
}

