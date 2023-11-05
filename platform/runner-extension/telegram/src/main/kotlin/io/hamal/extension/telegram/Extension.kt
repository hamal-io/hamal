package io.hamal.extension.telegram

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extension.ExtensionConfig
import io.hamal.lib.kua.extension.script.RunnerScriptExtension
import io.hamal.lib.kua.extension.script.RunnerScriptExtensionFactory
import io.hamal.lib.kua.type.StringType


object TelegramScriptFactory : RunnerScriptExtensionFactory {
    override fun create(sandbox: Sandbox): RunnerScriptExtension {
        return RunnerScriptExtension(
            name = "telegram",
            config = ExtensionConfig(
                mutableMapOf(
                    "base_url" to StringType("https://api.telegram.org")
                )
            )
        )
    }
}

