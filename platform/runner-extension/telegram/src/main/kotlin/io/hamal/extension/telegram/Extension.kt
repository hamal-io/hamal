package io.hamal.extension.telegram

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extend.ExtensionConfig
import io.hamal.lib.kua.extend.extension.RunnerExtension
import io.hamal.lib.kua.extend.extension.RunnerExtensionFactory
import io.hamal.lib.kua.type.StringType


object TelegramScriptFactory : RunnerExtensionFactory {
    override fun create(sandbox: Sandbox): RunnerExtension {
        return RunnerExtension(
            name = "telegram",
            config = ExtensionConfig(
                mutableMapOf(
                    "base_url" to StringType("https://api.telegram.org")
                )
            )
        )
    }
}

