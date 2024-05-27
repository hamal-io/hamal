package io.hamal.extension.social.telegram

import io.hamal.lib.domain.vo.ExtensionName.Companion.ExtensionName
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extend.extension.RunnerExtension
import io.hamal.lib.kua.extend.extension.RunnerExtensionFactory


object ExtensionSocialTelegramFactory : RunnerExtensionFactory {
    override fun create(sandbox: Sandbox): RunnerExtension {
        return RunnerExtension(
            name = ExtensionName("social.telegram"),
//            nodes = listOf(SendMessageNode),
            nodeCompilers = listOf(
//                SendMessage
            )
        )
    }
}

