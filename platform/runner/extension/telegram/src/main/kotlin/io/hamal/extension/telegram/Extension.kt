package io.hamal.extension.telegram

import io.hamal.extension.telegram.nodes.SendMessageNode
import io.hamal.extension.telegram.nodes.SendMessageNodeGenerator
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extend.extension.RunnerExtension
import io.hamal.lib.kua.extend.extension.RunnerExtensionFactory
import io.hamal.lib.value.ValueString


object ExtensionTelegramFactory : RunnerExtensionFactory {
    override fun create(sandbox: Sandbox): RunnerExtension {
        return RunnerExtension(
            name = ValueString("telegram"),
            nodes = listOf(SendMessageNode),
            generators = listOf(SendMessageNodeGenerator)
        )
    }
}

