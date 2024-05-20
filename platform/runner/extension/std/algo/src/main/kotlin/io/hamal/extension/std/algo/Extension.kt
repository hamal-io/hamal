package io.hamal.extension.std.algo

import io.hamal.lib.domain.vo.ExtensionFile.Companion.ExtensionFile
import io.hamal.lib.domain.vo.ExtensionName.Companion.ExtensionName
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extend.extension.RunnerExtension
import io.hamal.lib.kua.extend.extension.RunnerExtensionFactory

object ExtensionStdAlgoFactory : RunnerExtensionFactory {
    override fun create(sandbox: Sandbox): RunnerExtension {
        return RunnerExtension(
            name = ExtensionName("std.algo"),
            files = listOf(
                ExtensionFile("list.lua"),
                ExtensionFile("graph.lua"),
                ExtensionFile("extension.lua"),
            )
        )
    }
}