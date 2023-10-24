package io.hamal.extension.safe.telegram

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extension.ExtensionConfig
import io.hamal.lib.kua.extension.safe.RunnerSafeExtension
import io.hamal.lib.kua.extension.safe.RunnerSafeExtensionFactory
import io.hamal.lib.kua.type.StringType


object TelegramSafeFactory : RunnerSafeExtensionFactory {
    override fun create(sandbox: Sandbox): RunnerSafeExtension {
        return RunnerSafeExtension(
            name = "telegram",
            config = ExtensionConfig(
                mutableMapOf(
                    "base_url" to StringType("https://api.telegram.org")
                )
            )
        )
    }
}

