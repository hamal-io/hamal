package io.hamal.extension.social.telegram

import io.hamal.extension.social.telegram.nodes.SendMessage
import io.hamal.extension.social.telegram.nodes.SendMessageNode
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extend.extension.RunnerExtension
import io.hamal.lib.kua.extend.extension.RunnerExtensionFactory


object ExtensionSocialTelegramFactory : RunnerExtensionFactory {
    override fun create(sandbox: Sandbox): RunnerExtension {
        return RunnerExtension(
            name = ValueString("social.telegram"),
            nodes = listOf(SendMessageNode),
            nodeCompilers = listOf(
                SendMessage
            )
        )
    }
}

