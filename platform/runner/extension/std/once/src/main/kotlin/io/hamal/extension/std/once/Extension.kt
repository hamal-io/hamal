package io.hamal.extension.std.once

import io.hamal.extension.std.table.ExtensionStdTableFactory
import io.hamal.extension.std.`throw`.ExtensionStdThrowFactory
import io.hamal.lib.domain.vo.ExtensionName.Companion.ExtensionName
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extend.extension.RunnerExtension
import io.hamal.lib.kua.extend.extension.RunnerExtensionFactory


object ExtensionStdOnceFactory : RunnerExtensionFactory {
    override fun create(sandbox: Sandbox): RunnerExtension {
        return RunnerExtension(
            name = ExtensionName("std.once"),
            dependencies = listOf(ExtensionStdTableFactory, ExtensionStdThrowFactory)
        )
    }
}

